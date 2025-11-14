package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Pipeline;
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultRequest;
import vaatz.stereotypesdb.qa.dto.ResultSummaryResponse;
import vaatz.stereotypesdb.qa.repository.PipelineRepository;
import vaatz.stereotypesdb.qa.repository.ResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private final ResultRepository resultRepository;
    private final PipelineRepository pipelineRepository;

    public ResultService(ResultRepository resultRepository,
                         PipelineRepository pipelineRepository) {
        this.resultRepository = resultRepository;
        this.pipelineRepository = pipelineRepository;
    }

    public ResultSummaryResponse getSummary() {
        ResultSummaryResponse response = new ResultSummaryResponse();
        long total = resultRepository.count();
        Long completed = resultRepository.countByStatus("COMPLETED");
        Long failed = resultRepository.countByStatus("FAILED");
        response.setTotalDocuments(total);
        response.setCompletedDocuments(completed);
        response.setFailedDocuments(failed);
        return response;
    }

    public List<ResultDetailResponse> getAll(Long pipelineId) {
        List<Result> results;
        if (pipelineId != null) {
            results = resultRepository.findByPipelineId(pipelineId);
        } else {
            results = resultRepository.findAll();
        }
        return results.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public ResultDetailResponse getById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        return toDetailResponse(result);
    }

    public ResultDetailResponse create(ResultRequest request) {
        Pipeline pipeline = pipelineRepository.findById(request.getPipelineId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 파이프라인을 찾을 수 없습니다."));
        Result result = new Result();
        applyRequest(result, request);
        result.setPipeline(pipeline);
        return toDetailResponse(resultRepository.save(result));
    }

    public ResultDetailResponse update(Long id, ResultRequest request) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        Pipeline pipeline = pipelineRepository.findById(request.getPipelineId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 파이프라인을 찾을 수 없습니다."));
        applyRequest(result, request);
        result.setPipeline(pipeline);
        return toDetailResponse(resultRepository.save(result));
    }

    public void delete(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        resultRepository.delete(result);
    }

    private void applyRequest(Result result, ResultRequest request) {
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

    private ResultDetailResponse toDetailResponse(Result result) {
        ResultDetailResponse response = new ResultDetailResponse();
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

