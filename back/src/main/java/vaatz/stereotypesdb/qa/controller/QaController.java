package vaatz.stereotypesdb.qa.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import vaatz.stereotypesdb.qa.dto.FileMoveRequest;
import vaatz.stereotypesdb.qa.dto.HtmlFileSaveRequest;
import vaatz.stereotypesdb.qa.dto.HtmlUpdateRequest;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileContentResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFolderResponse;
import vaatz.stereotypesdb.qa.model.HtmlSheetData;
import vaatz.stereotypesdb.qa.model.WorkspaceFolderType;
import vaatz.stereotypesdb.qa.service.FileWorkspaceService;
import vaatz.stereotypesdb.qa.util.JsonlConverter;

/**
* @ClassName	: QaController.java
* @Description	: QA 파일 관리 관련 REST API 컨트롤러
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 엑셀 파일 업로드 및 HTML 변환 API
* 								- 파일 목록 조회, 상세 조회, 삭제 API
* 								- 피드백 저장, 시트 편집 저장 API
* 								- HTML을 JSONL 형식으로 변환하는 API
*/
@Validated
@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final FileWorkspaceService fileWorkspaceService;

    public QaController(FileWorkspaceService fileWorkspaceService) {
        this.fileWorkspaceService = fileWorkspaceService;
    }

    /**
     * after -> before -> dev 순으로 워크스페이스 현황을 반환한다.
     * before 폴더에만 검색 기능을 사용할 수 있다.
     */
    @GetMapping("/workspace")
    public ResponseEntity<List<WorkspaceFolderResponse>> getWorkspace(
            @RequestParam(defaultValue = "0") int afterPage,
            @RequestParam(defaultValue = "0") int beforePage,
            @RequestParam(defaultValue = "0") int devPage,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String beforeKeyword) {
        return ResponseEntity.ok(fileWorkspaceService.getWorkspaceOverview(afterPage, beforePage, devPage, size, beforeKeyword));
    }

    /**
     * HTML 파일을 업로드하여 before 폴더에 저장한다.
     * 단일 또는 다중 파일 업로드를 지원한다.
     */
    @PostMapping(value = "/files/html", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadHtml(@RequestPart(value = "file", required = false) MultipartFile file,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        // 다중 파일 업로드 우선 처리
        if (files != null && !files.isEmpty()) {
            List<WorkspaceFileResponse> responses = fileWorkspaceService.uploadMultipleHtml(files);
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        }
        // 단일 파일 업로드 (하위 호환성 유지)
        if (file != null) {
            WorkspaceFileResponse response = fileWorkspaceService.uploadHtml(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 파일이 필요합니다.");
    }

    /**
     * before 폴더의 HTML을 덮어쓴다.
     */
    @PutMapping("/files/before/{fileName}")
    public ResponseEntity<WorkspaceFileResponse> saveHtml(
            @PathVariable String fileName,
            @Valid @RequestBody HtmlFileSaveRequest request) {
        WorkspaceFileResponse response = fileWorkspaceService.saveHtmlContent(fileName, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 폴더/파일 내용을 그대로 반환한다.
     */
    @GetMapping("/files/{folder}/{fileName}")
    public ResponseEntity<WorkspaceFileContentResponse> readFile(
            @PathVariable String folder,
            @PathVariable String fileName) {
        WorkspaceFolderType folderType = WorkspaceFolderType.from(folder); //folder 문자열을 workspaceFolderType으로 변환해서 해당 파일 읽기
        return ResponseEntity.ok(fileWorkspaceService.readFile(folderType, fileName));
    }

    /**
     * after 폴더에 있는 JSONL을 dev 폴더로 이동
     * 여러 파일을 한 번에 처리
     * 
     * 파일 이동 후, 프론트엔드에서 직접 /api/batch/start/inferenceResultJob을 호출
     * 응답으로 반환되는 파일 경로 정보를 사용하여 배치 API를 호출
     */
    @PostMapping("/files/after/promote")
    public ResponseEntity<?> moveAfterToDev(@Valid @RequestBody FileMoveRequest request) {
        List<String> fileList = request.getFileList();
        
        // 각 파일을 이동하고 배치 API 호출에 필요한 경로 정보를 반환
        List<Map<String, String>> result = new ArrayList<>();
        
        for (String jsonlFileName : fileList) {
            // 1. after 폴더에서 dev 폴더로 파일 이동
            WorkspaceFileResponse movedFile = fileWorkspaceService.moveAfterToDev(jsonlFileName);
            
            // 2. dev 폴더의 jsonl 파일 경로 가져오기 (C:\Dev\doc\jsonl\file.jsonl)
            String jsonlFilePath = movedFile.getAbsolutePath();
            
            // 3. maskedFilePath 생성 (jsonl 파일명에서 추론)
            // 예: file.jsonl -> C:\Dev\doc\masked\file.xlsx
            String maskedFilePath = fileWorkspaceService.generateMaskedFilePath(jsonlFileName);
            
            // 4. 프론트엔드에서 배치 API 호출에 사용할 데이터 반환
            Map<String, String> fileData = new HashMap<>();
            fileData.put("maskedFilePath", maskedFilePath);
            fileData.put("jsonlFilePath", jsonlFilePath);
            result.add(fileData);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 수정된 HTML 테이블을 JSONL로 변환하고 after 폴더에 동일 파일을 저장한다.
     */
    @PostMapping("/convert/jsonl") //HtmlUpdateRequest -> HtmlSheetsData 리스트로 변환
    public ResponseEntity<ByteArrayResource> convertToJsonl(@Valid @RequestBody HtmlUpdateRequest request) {
        List<HtmlSheetData> sheets = toSheetData(request);
        byte[] jsonlBytes = JsonlConverter.toJsonlBytes(sheets);
        ByteArrayResource resource = new ByteArrayResource(jsonlBytes);
        //jsonl bytes, jsonlFileName 원본 파일
        String jsonlFileName = resolveJsonlFileName(request.getFileName());
        fileWorkspaceService.saveJsonlToAfter(jsonlFileName, jsonlBytes);
        ContentDisposition disposition = ContentDisposition.attachment().filename(jsonlFileName,StandardCharsets.UTF_8).build();  
        return ResponseEntity.ok() //Tomcat 한글 제목 인코딩 오류 수정
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,disposition.toString())
                .contentLength(jsonlBytes.length)
                .body(resource);
    }

    /**
     * HtmlUpdateRequest를 HtmlSheetData 리스트로 변환한다.
     * 
     * 변환 규칙:
     * 1. 테이블이 1개인 경우: 기존 방식 유지 (하나의 HtmlSheetData로 변환)
     * 2. 테이블이 2개 이상인 경우: 각 테이블을 별도의 HtmlSheetData로 분리
     *    - 각 테이블마다 시트명에 번호가 자동 추가됨 (예: "클러스터" -> "클러스터1", "클러스터2", "클러스터3")
     *    - 각 테이블별로 별도의 이미지 사용 (imageBase64List 제공 시)
     *    - imageBase64List가 없으면 원본 imageBase64를 모든 테이블에 공유 (하위 호환성)
     * 
     * 결과: 각 HtmlSheetData는 JSONL 파일의 한 줄이 됨
     * 
     * @Date: 2025.11.25
     */
    private List<HtmlSheetData> toSheetData(HtmlUpdateRequest request) {
        if (request.getSheets() == null || request.getSheets().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시트 데이터가 필요합니다.");
        }
        
        List<HtmlSheetData> result = new ArrayList<>();
        
        for (HtmlUpdateRequest.SheetHtmlUpdate sheet : request.getSheets()) {
            String htmlContent = sheet.getHtmlContent();
            List<JsonlConverter.TableSegment> tableSegments = JsonlConverter.extractTableSegments(htmlContent);
            int tableCount = tableSegments.size();
            
            // 테이블이 2개 이상인 경우: 각 테이블을 별도의 HtmlSheetData로 분리
            if (tableCount >= 2) {
                // 프론트엔드에서 제공한 각 테이블별 이미지 리스트 (null일 수 있음)
                List<String> imageList = sheet.getImageBase64List();
                
                // 각 테이블에 대해 반복 처리
                for (int i = 0; i < tableSegments.size(); i++) {
                    String singleTableHtml = buildHtmlWithSingleTable(htmlContent, tableSegments, i);
                    
                    // 시트명에 번호 추가
                    // 예: 원본 시트명이 "클러스터"이고 테이블이 3개인 경우
                    //     -> "클러스터1", "클러스터2", "클러스터3"
                    String sheetName = sheet.getSheetName() + (i + 1);
                    
                    // 각 테이블별 이미지 결정
                    // 1순위: imageBase64List에서 해당 인덱스의 이미지 사용 (프론트엔드에서 각 테이블별 이미지 제공 시)
                    // 2순위: imageBase64 사용 (하위 호환성: 원본 이미지를 모든 테이블에 공유)
                    String tableImage = null;
                    if (imageList != null && imageList.size() > i) {
                        // 각 테이블별 별도 이미지 사용
                        tableImage = imageList.get(i);
                    } else {
                        // 하위 호환성: 원본 이미지를 모든 테이블에 공유
                        tableImage = sheet.getImageBase64();
                    }
                    // 분리된 테이블을 별도의 HtmlSheetData로 생성
                    // 이 데이터는 JSONL 파일의 한 줄이 됨
                    result.add(new HtmlSheetData(sheetName, singleTableHtml, tableImage));
                }
            } else {
                // 테이블이 1개인 경우: 기존 방식 유지
                // 하나의 HtmlSheetData로 변환 (시트명 번호 추가 없음)
                result.add(new HtmlSheetData(
                        sheet.getSheetName(),
                        sheet.getHtmlContent(),
                        sheet.getImageBase64()));
            }
        }
        
        return result;
    }

    /**
     * 원본 파일명에서 JSONL 파일명을 생성한다.
     * 
     */
    private String resolveJsonlFileName(String sourceFileName) {
        if (sourceFileName == null || sourceFileName.trim().isEmpty()) {
            return "output.jsonl"; //기본 값
        }
        String trimmed = sourceFileName.trim();
        int idx = trimmed.lastIndexOf('.');
        if (idx > 0) {
            return trimmed.substring(0, idx) + ".jsonl";
        }
        return trimmed + ".jsonl"; //원본 파일명에서 .jsonl로 교체 
    }

    /**
     * 다중 테이블 HTML에서 지정된 테이블만 남기고 전체 DOCTYPE/HEAD 구조를 유지한다.
     */
    private String buildHtmlWithSingleTable(String originalHtml, List<JsonlConverter.TableSegment> segments, int targetIndex) {
        if (originalHtml == null) {
            return null;
        }
        if (segments == null || segments.isEmpty()) {
            return originalHtml;
        }
        if (targetIndex < 0 || targetIndex >= segments.size()) {
            return originalHtml;
        }
        
        JsonlConverter.TableSegment targetSegment = segments.get(targetIndex);

        // body 태그 범위를 찾아 선택된 테이블만 남기고 나머지 본문을 제거한다.
        String lowerHtml = originalHtml.toLowerCase();
        int bodyOpenIdx = lowerHtml.indexOf("<body");
        if (bodyOpenIdx < 0) {
            return targetSegment.getHtml();
        }
        int bodyStart = originalHtml.indexOf('>', bodyOpenIdx);
        if (bodyStart < 0) {
            return targetSegment.getHtml();
        }
        int bodyCloseIdx = lowerHtml.indexOf("</body", bodyStart);
        if (bodyCloseIdx < 0) {
            return targetSegment.getHtml();
        }

        StringBuilder builder = new StringBuilder(originalHtml.length());
        builder.append(originalHtml, 0, bodyStart + 1);
        builder.append('\n').append(targetSegment.getHtml()).append('\n');
        builder.append(originalHtml, bodyCloseIdx, originalHtml.length());
        return builder.toString();
    }
}
