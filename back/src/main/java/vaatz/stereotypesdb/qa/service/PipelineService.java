package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Model;
import vaatz.stereotypesdb.qa.domain.Pipeline;
import vaatz.stereotypesdb.qa.dto.PipelineRequest;
import vaatz.stereotypesdb.qa.repository.PipelineRepository;

import java.util.List;

@Service
@Transactional
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final ModelService modelService;

    public PipelineService(PipelineRepository pipelineRepository, ModelService modelService) {
        this.pipelineRepository = pipelineRepository;
        this.modelService = modelService;
    }

    public List<Pipeline> getAll() {
        return pipelineRepository.findAll();
    }

    public Pipeline getById(Long id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 파이프라인을 찾을 수 없습니다."));
    }

    public List<Pipeline> getByModel(Long modelId) {
        return pipelineRepository.findByModelId(modelId);
    }

    public Pipeline create(PipelineRequest request) {
        Model model = modelService.getById(request.getModelId());
        Pipeline pipeline = new Pipeline();
        pipeline.setName(request.getName());
        pipeline.setStatus(request.getStatus());
        pipeline.setConfiguration(request.getConfiguration());
        pipeline.setModel(model);
        return pipelineRepository.save(pipeline);
    }

    public Pipeline update(Long id, PipelineRequest request) {
        Pipeline pipeline = getById(id);
        Model model = modelService.getById(request.getModelId());
        pipeline.setName(request.getName());
        pipeline.setStatus(request.getStatus());
        pipeline.setConfiguration(request.getConfiguration());
        pipeline.setModel(model);
        return pipelineRepository.save(pipeline);
    }

    public void delete(Long id) {
        Pipeline pipeline = getById(id);
        pipelineRepository.delete(pipeline);
    }
}
