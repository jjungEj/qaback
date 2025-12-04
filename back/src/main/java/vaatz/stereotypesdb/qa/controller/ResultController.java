package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vaatz.stereotypesdb.qa.dto.HtmlFileSaveRequest;
import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultPageResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.service.FileWorkspaceService;
import vaatz.stereotypesdb.qa.service.ResultService;

/**
* @ClassName	: ResultController.java
* @Description	: 결과 조회 + QA 전달 REST API 컨트롤러
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 결과 조회 엔드포인트 정의 (향후 구현 예정)
* 2025.12.04        GPT-5.1 Codex - 목록/상세/QA 전달 구현
*/
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;
    private final FileWorkspaceService fileWorkspaceService;

    public ResultController(ResultService resultService, FileWorkspaceService fileWorkspaceService) {
        this.resultService = resultService;
        this.fileWorkspaceService = fileWorkspaceService;
    }

    /**
     * 결과 목록 조회 (기본 10건씩, 페이지네이션 정보 포함)
     */
    @GetMapping
    public ResponseEntity<ResultPageResponse> getResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(resultService.getResultPage(page, size));
    }

    /**
     * 결과 상세 조회 (문단 3개는 현재 빈칸)
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<ResultDetailResponse> getResultDetail(@PathVariable String fileName) {
        return ResponseEntity.ok(resultService.getResultDetail(fileName));
    }

    /**
     * 결과 -> QA before 폴더로 전달
     */
    @PostMapping("/{fileName}/qa")
    public ResponseEntity<WorkspaceFileResponse> sendResultToQa(@PathVariable String fileName) {
        HtmlFileSaveRequest request = new HtmlFileSaveRequest();
        request.setHtmlContent(resultService.resolveQaHtmlContent(fileName));
        WorkspaceFileResponse saved = fileWorkspaceService.saveHtmlContent(fileName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
