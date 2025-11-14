package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Pipeline;
import vaatz.stereotypesdb.qa.dto.PipelineRequest;
import vaatz.stereotypesdb.qa.dto.PipelineResponse;
import vaatz.stereotypesdb.qa.repository.PipelineRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PipelineService {

    private final PipelineRepository pipelineRepository;

    public PipelineService(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    public List<PipelineResponse> getAll(String status) {
        List<Pipeline> pipelines;
        if (status != null) {
            pipelines = pipelineRepository.findByStatus(status);
        } else {
            pipelines = pipelineRepository.findAll();
        }
        return pipelines.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PipelineResponse getById(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인을 찾을 수 없습니다."));
        return toResponse(pipeline);
    }

    public PipelineResponse create(PipelineRequest request) {
        Pipeline pipeline = new Pipeline();
        applyRequest(pipeline, request);
        return toResponse(pipelineRepository.save(pipeline));
    }

    public PipelineResponse update(Long id, PipelineRequest request) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인을 찾을 수 없습니다."));
        applyRequest(pipeline, request);
        return toResponse(pipelineRepository.save(pipeline));
    }

    public void delete(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인을 찾을 수 없습니다."));
        pipelineRepository.delete(pipeline);
    }

    private void applyRequest(Pipeline pipeline, PipelineRequest request) {
        pipeline.setDocumentName(request.getDocumentName());
        pipeline.setStatus(request.getStatus());
        pipeline.setStartedAt(request.getStartedAt());
        pipeline.setFinishedAt(request.getFinishedAt());
        pipeline.setErrorMessage(request.getErrorMessage());

        if (request.getStartedAt() != null && request.getFinishedAt() != null) {
            Duration duration = Duration.between(request.getStartedAt(), request.getFinishedAt());
            pipeline.setDurationSeconds(duration.getSeconds());
        } else {
            pipeline.setDurationSeconds(null);
        }
    }

    private PipelineResponse toResponse(Pipeline pipeline) {
        PipelineResponse response = new PipelineResponse();
        response.setId(pipeline.getId());
        response.setDocumentName(pipeline.getDocumentName());
        response.setStatus(pipeline.getStatus());
        response.setStartedAt(pipeline.getStartedAt());
        response.setFinishedAt(pipeline.getFinishedAt());
        response.setDurationSeconds(pipeline.getDurationSeconds());
        response.setErrorMessage(pipeline.getErrorMessage());
        return response;
    }
}

