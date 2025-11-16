package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.QaComment;
import vaatz.stereotypesdb.qa.domain.QaDocument;
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.dto.QaCommentRequest;
import vaatz.stereotypesdb.qa.dto.QaCommentResponse;
import vaatz.stereotypesdb.qa.dto.QaDocumentRequest;
import vaatz.stereotypesdb.qa.dto.QaDocumentResponse;
import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.repository.QaCommentRepository;
import vaatz.stereotypesdb.qa.repository.QaDocumentRepository;
import vaatz.stereotypesdb.qa.repository.ResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QaService {

    private final QaDocumentRepository qaDocumentRepository;
    private final QaCommentRepository qaCommentRepository;
    private final ResultRepository resultRepository;
    private final ResultService resultService;

    public QaService(QaDocumentRepository qaDocumentRepository,
                     QaCommentRepository qaCommentRepository,
                     ResultRepository resultRepository,
                     ResultService resultService) {
        this.qaDocumentRepository = qaDocumentRepository;
        this.qaCommentRepository = qaCommentRepository;
        this.resultRepository = resultRepository;
        this.resultService = resultService;
    }

    // QA 목록 조회 - 로컬 파일에서 업로드된 Result 목록 반환
    public List<ResultDetailResponse> getQaResults() {
        return resultService.getAllFromLocalFiles();
    }

    // QA 상세 조회 - Result 상세 정보와 코멘트 포함
    public ResultDetailResponse getQaResultById(Long resultId) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        
        // 로컬 파일에서 업로드된 것인지 확인
        if (result.getPipeline() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "로컬 파일에서 업로드된 결과만 QA 대상입니다.");
        }
        
        return resultService.getById(resultId);
    }

    // QA 문서 상태 업데이트 (또는 생성)
    public QaDocumentResponse updateQaStatus(Long resultId, QaDocumentRequest request) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));

        QaDocument document = qaDocumentRepository.findByResultId(resultId)
                .orElseGet(() -> {
                    QaDocument newDoc = new QaDocument();
                    newDoc.setResult(result);
                    newDoc.setDocumentName(result.getDocumentName());
                    return newDoc;
                });

        document.setDocumentType(request.getDocumentType());
        document.setQaContent(request.getQaContent());
        document.setStatus(request.getStatus());

        return toResponse(qaDocumentRepository.save(document));
    }

    // 코멘트 추가
    public QaCommentResponse addComment(Long resultId, QaCommentRequest request) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));

        QaComment comment = new QaComment();
        comment.setComment(request.getComment());
        comment.setModifiedField(request.getModifiedField());
        comment.setResult(result);
        result.getQaComments().add(comment);

        resultRepository.save(result);

        return toCommentResponse(comment);
    }

    // 코멘트 목록 조회
    public List<QaCommentResponse> getComments(Long resultId) {
        List<QaComment> comments = qaCommentRepository.findByResultId(resultId);
        return comments.stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    // QA 문서 조회 (Result ID로)
    public QaDocumentResponse getQaDocumentByResultId(Long resultId) {
        QaDocument document = qaDocumentRepository.findByResultId(resultId)
                .orElse(null);
        
        if (document == null) {
            // QA 문서가 없으면 기본값 반환
            Result result = resultRepository.findById(resultId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
            QaDocumentResponse response = new QaDocumentResponse();
            response.setResultId(resultId);
            response.setDocumentName(result.getDocumentName());
            response.setStatus("확인 필요");
            return response;
        }
        
        return toResponse(document);
    }

    private QaDocumentResponse toResponse(QaDocument document) {
        QaDocumentResponse response = new QaDocumentResponse();
        response.setId(document.getId());
        response.setDocumentName(document.getDocumentName());
        response.setDocumentType(document.getDocumentType());
        response.setQaContent(document.getQaContent());
        response.setStatus(document.getStatus());
        response.setResultId(document.getResult() != null ? document.getResult().getId() : null);
        response.setComments(document.getResult() != null ? 
                document.getResult().getQaComments().stream()
                        .map(this::toCommentResponse)
                        .collect(Collectors.toList()) : List.of());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }

    private QaCommentResponse toCommentResponse(QaComment comment) {
        QaCommentResponse response = new QaCommentResponse();
        response.setId(comment.getId());
        response.setComment(comment.getComment());
        response.setModifiedField(comment.getModifiedField());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}

