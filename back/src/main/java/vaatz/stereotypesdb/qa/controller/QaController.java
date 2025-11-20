package vaatz.stereotypesdb.qa.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vaatz.stereotypesdb.qa.dto.*;
import vaatz.stereotypesdb.qa.service.QaFileInfoService;
import vaatz.stereotypesdb.qa.util.ExcelToHtmlConverter;
import vaatz.stereotypesdb.qa.util.JsonlConverter;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
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

    private final QaFileInfoService qaFileInfoService;

    public QaController(QaFileInfoService qaFileInfoService) {
        this.qaFileInfoService = qaFileInfoService;
    }

    // 엑셀 파일 업로드 및 HTML 변환 (DB 저장) - 단일 파일 (하위 호환성 유지)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcel(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") 
                && !fileName.toLowerCase().endsWith(".xls") 
                && !fileName.toLowerCase().endsWith(".csv"))) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            QaFileInfoResponse response = qaFileInfoService.uploadFile(
                    fileName, file.getSize(), fileType, file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            if (e.getStatus() == HttpStatus.CONFLICT) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("이미 업로드한 파일입니다: " + fileName);
            }
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 다중 파일 업로드
    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MultiUploadResponse> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().build();
        }
        
        MultiUploadResponse response = qaFileInfoService.uploadMultipleFiles(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // HTML 파일 업로드 (이미 테이블 형태의 HTML 저장) - 단일 파일 (하위 호환성 유지)
    @PostMapping(value = "/upload/html", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadHtml(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.toLowerCase().endsWith(".html")
                && !fileName.toLowerCase().endsWith(".htm"))) {
            return ResponseEntity.badRequest().build();
        }

        try {
            QaFileInfoResponse response = qaFileInfoService.uploadHtmlFile(
                    fileName, file.getSize(), file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            if (e.getStatus() == HttpStatus.CONFLICT) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("이미 업로드한 파일입니다: " + fileName);
            }
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 파일 목록 조회 (전체, 하위 호환성 유지)
    @GetMapping("/files")
    public ResponseEntity<List<QaFileInfoResponse>> getFileList() {
        return ResponseEntity.ok(qaFileInfoService.getAllFiles());
    }
    
    // 파일 목록 조회 (페이징)
    @GetMapping("/files/paged")
    public ResponseEntity<PageResponse<QaFileInfoResponse>> getFileListWithPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(qaFileInfoService.getFilesWithPaging(page, size));
    }
    
    // 파일 검색 (페이징)
    @GetMapping("/files/search")
    public ResponseEntity<PageResponse<QaFileInfoResponse>> searchFiles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(qaFileInfoService.searchFiles(keyword, page, size));
    }

    // 파일 상세 조회
    @GetMapping("/files/{id}")
    public ResponseEntity<QaFileInfoResponse> getFileDetail(@PathVariable Long id) {
        return ResponseEntity.ok(qaFileInfoService.getFileById(id));
    }

    // 피드백 저장
    @PutMapping("/files/{id}/feedback")
    public ResponseEntity<QaFileInfoResponse> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(qaFileInfoService.updateFeedback(id, request));
    }

    // 편집된 시트 저장
    @PutMapping("/files/{id}/sheets")
    public ResponseEntity<QaFileInfoResponse> updateSheets(
            @PathVariable Long id,
            @Valid @RequestBody vaatz.stereotypesdb.qa.dto.SheetsUpdateRequest request) {
        return ResponseEntity.ok(qaFileInfoService.updateSheets(id, request));
    }

    // 파일 삭제
    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        qaFileInfoService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    // 수정된 HTML을 JSONL 형식으로 변환
    @PostMapping("/convert/jsonl")
    public ResponseEntity<ByteArrayResource> convertToJsonl(@Valid @RequestBody HtmlUpdateRequest request) {
        try {
            List<ExcelToHtmlConverter.SheetData> sheets = request.getSheets().stream()
                    .map(sheet -> new ExcelToHtmlConverter.SheetData(
                            sheet.getSheetName(), 
                            sheet.getHtmlContent(), 
                            sheet.getImageBase64())) // 프론트엔드에서 전달한 base64 이미지 사용
                    .collect(Collectors.toList());

            byte[] jsonlBytes = JsonlConverter.toJsonlBytes(sheets);
            ByteArrayResource resource = new ByteArrayResource(jsonlBytes);
            
            String jsonlFileName = request.getFileName();
            if (jsonlFileName != null && jsonlFileName.contains(".")) {
                jsonlFileName = jsonlFileName.substring(0, jsonlFileName.lastIndexOf('.')) + ".jsonl";
            } else {
                jsonlFileName = "output.jsonl";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + jsonlFileName + "\"")
                    .contentLength(jsonlBytes.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
