package vaatz.stereotypesdb.qa.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
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
import vaatz.stereotypesdb.qa.dto.WorkspaceFileContentResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFolderResponse;
import vaatz.stereotypesdb.qa.model.HtmlSheetData;
import vaatz.stereotypesdb.qa.model.WorkspaceFolderType;
import vaatz.stereotypesdb.qa.service.FileWorkspaceService;
import vaatz.stereotypesdb.qa.util.JsonlConverter;

import javax.validation.Valid;
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

    private final FileWorkspaceService fileWorkspaceService;

    public QaController(FileWorkspaceService fileWorkspaceService) {
        this.fileWorkspaceService = fileWorkspaceService;
    }

    /**
     * after -> before -> dev 순으로 워크스페이스 현황을 반환한다.
     */
    @GetMapping("/workspace")
    public ResponseEntity<List<WorkspaceFolderResponse>> getWorkspace(
            @RequestParam(defaultValue = "0") int afterPage,
            @RequestParam(defaultValue = "0") int beforePage,
            @RequestParam(defaultValue = "0") int devPage,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(fileWorkspaceService.getWorkspaceOverview(afterPage, beforePage, devPage, size));
    }

    /**
     * HTML 파일을 업로드하여 before 폴더에 저장한다.
     */
    @PostMapping(value = "/files/html", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WorkspaceFileResponse> uploadHtml(@RequestPart("file") MultipartFile file) {
        WorkspaceFileResponse response = fileWorkspaceService.uploadHtml(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
     * after 폴더에 있는 JSONL을 dev 경로로 이동한다.
     */
    @PostMapping("/files/after/promote")
    public ResponseEntity<WorkspaceFileResponse> moveAfterToDev(@Valid @RequestBody FileMoveRequest request) {
        WorkspaceFileResponse response = fileWorkspaceService.moveAfterToDev(request.getFileName());
        return ResponseEntity.ok(response);
    }

    /**
     * 수정된 HTML 테이블을 JSONL로 변환하고 after 폴더에 동일 파일을 저장한다.
     */
    @PostMapping("/convert/jsonl")
    public ResponseEntity<ByteArrayResource> convertToJsonl(@Valid @RequestBody HtmlUpdateRequest request) {
        List<HtmlSheetData> sheets = toSheetData(request);
        byte[] jsonlBytes = JsonlConverter.toJsonlBytes(sheets);
        ByteArrayResource resource = new ByteArrayResource(jsonlBytes);

        String jsonlFileName = resolveJsonlFileName(request.getFileName());
        fileWorkspaceService.saveJsonlToAfter(jsonlFileName, jsonlBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + jsonlFileName + "\"")
                .contentLength(jsonlBytes.length)
                .body(resource);
    }

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

    private String resolveJsonlFileName(String sourceFileName) {
        if (sourceFileName == null || sourceFileName.trim().isEmpty()) {
            return "output.jsonl";
        }
        String trimmed = sourceFileName.trim();
        int idx = trimmed.lastIndexOf('.');
        if (idx > 0) {
            return trimmed.substring(0, idx) + ".jsonl";
        }
        return trimmed + ".jsonl";
    }
}
