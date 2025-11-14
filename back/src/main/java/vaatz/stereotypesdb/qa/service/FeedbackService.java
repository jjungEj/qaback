package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Feedback;
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.dto.FeedbackRequest;
import vaatz.stereotypesdb.qa.dto.FeedbackResponse;
import vaatz.stereotypesdb.qa.repository.FeedbackRepository;
import vaatz.stereotypesdb.qa.repository.ResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ResultRepository resultRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           ResultRepository resultRepository) {
        this.feedbackRepository = feedbackRepository;
        this.resultRepository = resultRepository;
    }

    public List<FeedbackResponse> getAll(Long resultId) {
        List<Feedback> entries;
        if (resultId != null) {
            entries = feedbackRepository.findByResultId(resultId);
        } else {
            entries = feedbackRepository.findAll();
        }
        return entries.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FeedbackResponse getById(Long id) {
        Feedback entry = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
        return toResponse(entry);
    }

    public FeedbackResponse create(FeedbackRequest request) {
        Result result = resultRepository.findById(request.getResultId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 결과를 찾을 수 없습니다."));
        Feedback entry = new Feedback();
        applyRequest(entry, request);
        entry.setResult(result);
        Feedback saved = feedbackRepository.save(entry);
        applyResultUpdate(result, request);
        return toResponse(saved);
    }

    public FeedbackResponse update(Long id, FeedbackRequest request) {
        Feedback entry = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
        Result result = resultRepository.findById(request.getResultId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 결과를 찾을 수 없습니다."));
        applyRequest(entry, request);
        entry.setResult(result);
        Feedback saved = feedbackRepository.save(entry);
        applyResultUpdate(result, request);
        return toResponse(saved);
    }

    public void delete(Long id) {
        Feedback entry = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
        feedbackRepository.delete(entry);
    }

    private void applyRequest(Feedback entry, FeedbackRequest request) {
        entry.setDocumentName(request.getDocumentName());
        entry.setLogType(request.getLogType());
        entry.setFeedback(request.getFeedback());
        entry.setStatus(request.getStatus());
    }

    private void applyResultUpdate(Result result, FeedbackRequest request) {
        boolean dirty = false;
        if (request.getUpdatedResultStatus() != null) {
            result.setStatus(request.getUpdatedResultStatus());
            dirty = true;
        }
        if (request.getUpdatedMetadata() != null) {
            result.setMetadata(request.getUpdatedMetadata());
            dirty = true;
        }
        if (dirty) {
            resultRepository.save(result);
        }
    }

    private FeedbackResponse toResponse(Feedback entry) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(entry.getId());
        response.setDocumentName(entry.getDocumentName());
        response.setLogType(entry.getLogType());
        response.setFeedback(entry.getFeedback());
        response.setStatus(entry.getStatus());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }
}

