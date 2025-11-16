package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vaatz.stereotypesdb.qa.dto.QaCommentRequest;
import vaatz.stereotypesdb.qa.dto.QaCommentResponse;
import vaatz.stereotypesdb.qa.dto.QaDocumentRequest;
import vaatz.stereotypesdb.qa.dto.QaDocumentResponse;
import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.service.QaService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    // QA 목록 조회 - 로컬 파일에서 업로드된 Result 목록
    @GetMapping
    public ResponseEntity<List<ResultDetailResponse>> getQaResults() {
        return ResponseEntity.ok(qaService.getQaResults());
    }

    // QA 상세 조회 - Result 상세 정보
    @GetMapping("/results/{resultId}")
    public ResponseEntity<ResultDetailResponse> getQaResult(@PathVariable Long resultId) {
        return ResponseEntity.ok(qaService.getQaResultById(resultId));
    }

    // QA 문서 정보 조회 (Result ID로)
    @GetMapping("/results/{resultId}/document")
    public ResponseEntity<QaDocumentResponse> getQaDocument(@PathVariable Long resultId) {
        return ResponseEntity.ok(qaService.getQaDocumentByResultId(resultId));
    }

    // QA 상태 업데이트
    @PutMapping("/results/{resultId}/status")
    public ResponseEntity<QaDocumentResponse> updateQaStatus(@PathVariable Long resultId,
                                                             @Valid @RequestBody QaDocumentRequest request) {
        return ResponseEntity.ok(qaService.updateQaStatus(resultId, request));
    }

    // 코멘트 추가
    @PostMapping("/results/{resultId}/comments")
    public ResponseEntity<QaCommentResponse> addComment(@PathVariable Long resultId,
                                                        @Valid @RequestBody QaCommentRequest request) {
        QaCommentResponse created = qaService.addComment(resultId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 코멘트 목록 조회
    @GetMapping("/results/{resultId}/comments")
    public ResponseEntity<List<QaCommentResponse>> getComments(@PathVariable Long resultId) {
        return ResponseEntity.ok(qaService.getComments(resultId));
    }
}

