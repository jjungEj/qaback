package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vaatz.stereotypesdb.qa.dto.PipelineRequest;
import vaatz.stereotypesdb.qa.dto.PipelineResponse;
import vaatz.stereotypesdb.qa.service.PipelineService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    private final PipelineService pipelineService;

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @GetMapping
    public ResponseEntity<List<PipelineResponse>> getPipelines(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(pipelineService.getAll(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PipelineResponse> getPipeline(@PathVariable Long id) {
        return ResponseEntity.ok(pipelineService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PipelineResponse> createPipeline(@Valid @RequestBody PipelineRequest request) {
        PipelineResponse created = pipelineService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PipelineResponse> updatePipeline(@PathVariable Long id,
                                                           @Valid @RequestBody PipelineRequest request) {
        return ResponseEntity.ok(pipelineService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePipeline(@PathVariable Long id) {
        pipelineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

