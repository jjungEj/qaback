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
import vaatz.stereotypesdb.qa.dto.PipelineHistoryRequest;
import vaatz.stereotypesdb.qa.dto.PipelineHistoryResponse;
import vaatz.stereotypesdb.qa.service.PipelineHistoryService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pipeline-history")
public class PipelineHistoryController {

    private final PipelineHistoryService pipelineHistoryService;

    public PipelineHistoryController(PipelineHistoryService pipelineHistoryService) {
        this.pipelineHistoryService = pipelineHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<PipelineHistoryResponse>> getHistories(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(pipelineHistoryService.getAll(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PipelineHistoryResponse> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(pipelineHistoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PipelineHistoryResponse> createHistory(@Valid @RequestBody PipelineHistoryRequest request) {
        PipelineHistoryResponse created = pipelineHistoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PipelineHistoryResponse> updateHistory(@PathVariable Long id,
                                                                 @Valid @RequestBody PipelineHistoryRequest request) {
        return ResponseEntity.ok(pipelineHistoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        pipelineHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

