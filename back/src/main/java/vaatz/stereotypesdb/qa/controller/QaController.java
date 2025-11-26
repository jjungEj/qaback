package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.dto.FileMoveRequest;
import vaatz.stereotypesdb.qa.dto.HtmlFileSaveRequest;
import vaatz.stereotypesdb.qa.dto.HtmlUpdateRequest;
import vaatz.stereotypesdb.qa.dto.JsonlGenerationResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileContentResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFolderResponse;
import vaatz.stereotypesdb.qa.model.HtmlSheetData;
import vaatz.stereotypesdb.qa.model.JsonlFileMetadata;
import vaatz.stereotypesdb.qa.model.WorkspaceFolderType;
import vaatz.stereotypesdb.qa.service.FileWorkspaceService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        WorkspaceFolderType folderType = WorkspaceFolderType.from(folder);
        return ResponseEntity.ok(fileWorkspaceService.readFile(folderType, fileName));
    }

    /**
     * after 폴더에 있는 JSONL을 dev 폴더로 이동한다.
     * 여러 파일을 한 번에 처리할 수 있습니다.
     * 
     * 파일 이동 후, 프론트엔드에서 직접 /api/batch/start/inferenceResultJob을 호출해야 합니다.
     * 응답으로 반환되는 파일 경로 정보를 사용하여 배치 API를 호출하세요.
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
     * 수정된 HTML 테이블을 JSONL로 변환하고 시트별 JSONL을 after 폴더에 저장한다.
     */
    @PostMapping("/convert/jsonl")
    public ResponseEntity<JsonlGenerationResponse> convertToJsonl(@Valid @RequestBody HtmlUpdateRequest request) {
        List<HtmlSheetData> sheets = toSheetData(request);
        List<JsonlFileMetadata> generatedFiles = fileWorkspaceService.saveJsonlFilesBySheet(sheets);

        List<JsonlGenerationResponse.FileEntry> fileEntries = generatedFiles.stream()
                .map(meta -> new JsonlGenerationResponse.FileEntry(meta.getFileName(), meta.getAbsolutePath()))
                .collect(Collectors.toList());

        JsonlGenerationResponse response = new JsonlGenerationResponse(fileEntries);
        return ResponseEntity.ok(response);
    }

    /**
     * HtmlUpdateRequest를 HtmlSheetData 리스트로 변환한다.
     * @Date: 2025.11.25
     */
    private List<HtmlSheetData> toSheetData(HtmlUpdateRequest request) {
        if (request.getSheets() == null || request.getSheets().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시트 데이터가 필요합니다.");
        }
        return request.getSheets().stream()
                .map(sheet -> new HtmlSheetData(
                        sheet.getSheetName(),
                        sheet.getHtmlContent(),
                        sheet.getImageBase64()))
                .collect(Collectors.toList());
    }

}
