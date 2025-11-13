package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.FeedbackEntry;
import vaatz.stereotypesdb.qa.domain.ProcessingResult;
import vaatz.stereotypesdb.qa.dto.FeedbackEntryRequest;
import vaatz.stereotypesdb.qa.dto.FeedbackEntryResponse;
import vaatz.stereotypesdb.qa.repository.FeedbackEntryRepository;
import vaatz.stereotypesdb.qa.repository.ProcessingResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackEntryService {

    private final FeedbackEntryRepository feedbackEntryRepository;
    private final ProcessingResultRepository processingResultRepository;

    public FeedbackEntryService(FeedbackEntryRepository feedbackEntryRepository,
                                ProcessingResultRepository processingResultRepository) {
        this.feedbackEntryRepository = feedbackEntryRepository;
        this.processingResultRepository = processingResultRepository;
    }

    public List<FeedbackEntryResponse> getAll(Long processingResultId) {
        List<FeedbackEntry> entries;
        if (processingResultId != null) {
            entries = feedbackEntryRepository.findByProcessingResultId(processingResultId);
        } else {
            entries = feedbackEntryRepository.findAll();
        }
        return entries.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FeedbackEntryResponse getById(Long id) {
        FeedbackEntry entry = feedbackEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
        return toResponse(entry);
    }

    public FeedbackEntryResponse create(FeedbackEntryRequest request) {
        ProcessingResult result = processingResultRepository.findById(request.getProcessingResultId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 결과를 찾을 수 없습니다."));
        FeedbackEntry entry = new FeedbackEntry();
        applyRequest(entry, request);
        entry.setProcessingResult(result);
        FeedbackEntry saved = feedbackEntryRepository.save(entry);
        applyResultUpdate(result, request);
        return toResponse(saved);
    }

    public FeedbackEntryResponse update(Long id, FeedbackEntryRequest request) {
        FeedbackEntry entry = feedbackEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
        ProcessingResult result = processingResultRepository.findById(request.getProcessingResultId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 결과를 찾을 수 없습니다."));
        applyRequest(entry, request);
        entry.setProcessingResult(result);
        FeedbackEntry saved = feedbackEntryRepository.save(entry);
        applyResultUpdate(result, request);
        return toResponse(saved);
    }

    public void delete(Long id) {
        FeedbackEntry entry = feedbackEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
        feedbackEntryRepository.delete(entry);
    }

    private void applyRequest(FeedbackEntry entry, FeedbackEntryRequest request) {
        entry.setDocumentName(request.getDocumentName());
        entry.setLogType(request.getLogType());
        entry.setFeedback(request.getFeedback());
        entry.setStatus(request.getStatus());
    }

    private void applyResultUpdate(ProcessingResult result, FeedbackEntryRequest request) {
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
            processingResultRepository.save(result);
        }
    }

    private FeedbackEntryResponse toResponse(FeedbackEntry entry) {
        FeedbackEntryResponse response = new FeedbackEntryResponse();
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

