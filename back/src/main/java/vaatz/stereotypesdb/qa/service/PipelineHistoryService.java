package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.PipelineHistory;
import vaatz.stereotypesdb.qa.dto.PipelineHistoryRequest;
import vaatz.stereotypesdb.qa.dto.PipelineHistoryResponse;
import vaatz.stereotypesdb.qa.repository.PipelineHistoryRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PipelineHistoryService {

    private final PipelineHistoryRepository pipelineHistoryRepository;

    public PipelineHistoryService(PipelineHistoryRepository pipelineHistoryRepository) {
        this.pipelineHistoryRepository = pipelineHistoryRepository;
    }

    public List<PipelineHistoryResponse> getAll(String status) {
        List<PipelineHistory> histories;
        if (status != null) {
            histories = pipelineHistoryRepository.findByStatus(status);
        } else {
            histories = pipelineHistoryRepository.findAll();
        }
        return histories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PipelineHistoryResponse getById(Long id) {
        PipelineHistory history = pipelineHistoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인 기록을 찾을 수 없습니다."));
        return toResponse(history);
    }

    public PipelineHistoryResponse create(PipelineHistoryRequest request) {
        PipelineHistory history = new PipelineHistory();
        applyRequest(history, request);
        return toResponse(pipelineHistoryRepository.save(history));
    }

    public PipelineHistoryResponse update(Long id, PipelineHistoryRequest request) {
        PipelineHistory history = pipelineHistoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인 기록을 찾을 수 없습니다."));
        applyRequest(history, request);
        return toResponse(pipelineHistoryRepository.save(history));
    }

    public void delete(Long id) {
        PipelineHistory history = pipelineHistoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인 기록을 찾을 수 없습니다."));
        pipelineHistoryRepository.delete(history);
    }

    private void applyRequest(PipelineHistory history, PipelineHistoryRequest request) {
        history.setDocumentName(request.getDocumentName());
        history.setStatus(request.getStatus());
        history.setStartedAt(request.getStartedAt());
        history.setFinishedAt(request.getFinishedAt());
        history.setErrorMessage(request.getErrorMessage());

        if (request.getStartedAt() != null && request.getFinishedAt() != null) {
            Duration duration = Duration.between(request.getStartedAt(), request.getFinishedAt());
            history.setDurationSeconds(duration.getSeconds());
        } else {
            history.setDurationSeconds(null);
        }
    }

    private PipelineHistoryResponse toResponse(PipelineHistory history) {
        PipelineHistoryResponse response = new PipelineHistoryResponse();
        response.setId(history.getId());
        response.setDocumentName(history.getDocumentName());
        response.setStatus(history.getStatus());
        response.setStartedAt(history.getStartedAt());
        response.setFinishedAt(history.getFinishedAt());
        response.setDurationSeconds(history.getDurationSeconds());
        response.setErrorMessage(history.getErrorMessage());
        return response;
    }
}

