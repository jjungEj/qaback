package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Pipeline;
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.dto.ResultRequest;
import vaatz.stereotypesdb.qa.repository.ResultRepository;

import java.util.List;

@Service
@Transactional
public class ResultService {

    private final ResultRepository resultRepository;
    private final PipelineService pipelineService;

    public ResultService(ResultRepository resultRepository, PipelineService pipelineService) {
        this.resultRepository = resultRepository;
        this.pipelineService = pipelineService;
    }

    public List<Result> getAll() {
        return resultRepository.findAll();
    }

    public Result getById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
    }

    public List<Result> getByPipeline(Long pipelineId) {
        return resultRepository.findByPipelineId(pipelineId);
    }

    public Result create(ResultRequest request) {
        Pipeline pipeline = pipelineService.getById(request.getPipelineId());
        Result result = new Result();
        result.setStatus(request.getStatus());
        result.setMetrics(request.getMetrics());
        result.setLog(request.getLog());
        result.setStartedAt(request.getStartedAt());
        result.setFinishedAt(request.getFinishedAt());
        result.setPipeline(pipeline);
        return resultRepository.save(result);
    }

    public Result update(Long id, ResultRequest request) {
        Result result = getById(id);
        Pipeline pipeline = pipelineService.getById(request.getPipelineId());
        result.setStatus(request.getStatus());
        result.setMetrics(request.getMetrics());
        result.setLog(request.getLog());
        result.setStartedAt(request.getStartedAt());
        result.setFinishedAt(request.getFinishedAt());
        result.setPipeline(pipeline);
        return resultRepository.save(result);
    }

    public void delete(Long id) {
        Result result = getById(id);
        resultRepository.delete(result);
    }
}
