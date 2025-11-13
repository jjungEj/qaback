package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.PipelineHistory;
import vaatz.stereotypesdb.qa.domain.ProcessingResult;
import vaatz.stereotypesdb.qa.dto.ProcessingResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ProcessingResultRequest;
import vaatz.stereotypesdb.qa.dto.ProcessingResultSummaryResponse;
import vaatz.stereotypesdb.qa.repository.PipelineHistoryRepository;
import vaatz.stereotypesdb.qa.repository.ProcessingResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcessingResultService {

    private final ProcessingResultRepository processingResultRepository;
    private final PipelineHistoryRepository pipelineHistoryRepository;

    public ProcessingResultService(ProcessingResultRepository processingResultRepository,
                                   PipelineHistoryRepository pipelineHistoryRepository) {
        this.processingResultRepository = processingResultRepository;
        this.pipelineHistoryRepository = pipelineHistoryRepository;
    }

    public ProcessingResultSummaryResponse getSummary() {
        ProcessingResultSummaryResponse response = new ProcessingResultSummaryResponse();
        long total = processingResultRepository.count();
        Long completed = processingResultRepository.countByStatus("COMPLETED");
        Long failed = processingResultRepository.countByStatus("FAILED");
        response.setTotalDocuments(total);
        response.setCompletedDocuments(completed);
        response.setFailedDocuments(failed);
        return response;
    }

    public List<ProcessingResultDetailResponse> getAll(Long pipelineHistoryId) {
        List<ProcessingResult> results;
        if (pipelineHistoryId != null) {
            results = processingResultRepository.findByPipelineHistoryId(pipelineHistoryId);
        } else {
            results = processingResultRepository.findAll();
        }
        return results.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public ProcessingResultDetailResponse getById(Long id) {
        ProcessingResult result = processingResultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        return toDetailResponse(result);
    }

    public ProcessingResultDetailResponse create(ProcessingResultRequest request) {
        PipelineHistory history = pipelineHistoryRepository.findById(request.getPipelineHistoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 파이프라인 기록을 찾을 수 없습니다."));
        ProcessingResult result = new ProcessingResult();
        applyRequest(result, request);
        result.setPipelineHistory(history);
        return toDetailResponse(processingResultRepository.save(result));
    }

    public ProcessingResultDetailResponse update(Long id, ProcessingResultRequest request) {
        ProcessingResult result = processingResultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        PipelineHistory history = pipelineHistoryRepository.findById(request.getPipelineHistoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 파이프라인 기록을 찾을 수 없습니다."));
        applyRequest(result, request);
        result.setPipelineHistory(history);
        return toDetailResponse(processingResultRepository.save(result));
    }

    public void delete(Long id) {
        ProcessingResult result = processingResultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        processingResultRepository.delete(result);
    }

    private void applyRequest(ProcessingResult result, ProcessingResultRequest request) {
        result.setDocumentName(request.getDocumentName());
        result.setStatus(request.getStatus());
        result.setActualFileSize(request.getActualFileSize());
        result.setTransferredFileSize(request.getTransferredFileSize());
        result.setStartedAt(request.getStartedAt());
        result.setFinishedAt(request.getFinishedAt());
        result.setOriginalFileName(request.getOriginalFileName());
        result.setOriginalFileSize(request.getOriginalFileSize());
        result.setConvertedFileSize(request.getConvertedFileSize());
        result.setOriginalViewerUri(request.getOriginalViewerUri());
        result.setHtmlRenderUri(request.getHtmlRenderUri());
        result.setMetadata(request.getMetadata());
    }

    private ProcessingResultDetailResponse toDetailResponse(ProcessingResult result) {
        ProcessingResultDetailResponse response = new ProcessingResultDetailResponse();
        response.setId(result.getId());
        response.setDocumentName(result.getDocumentName());
        response.setStatus(result.getStatus());
        response.setActualFileSize(result.getActualFileSize());
        response.setTransferredFileSize(result.getTransferredFileSize());
        response.setStartedAt(result.getStartedAt());
        response.setFinishedAt(result.getFinishedAt());
        response.setOriginalFileName(result.getOriginalFileName());
        response.setOriginalFileSize(result.getOriginalFileSize());
        response.setConvertedFileSize(result.getConvertedFileSize());
        response.setOriginalViewerUri(result.getOriginalViewerUri());
        response.setHtmlRenderUri(result.getHtmlRenderUri());
        response.setMetadata(result.getMetadata());
        return response;
    }
}

