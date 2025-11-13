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
import vaatz.stereotypesdb.qa.dto.ProcessingResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ProcessingResultRequest;
import vaatz.stereotypesdb.qa.dto.ProcessingResultSummaryResponse;
import vaatz.stereotypesdb.qa.service.ProcessingResultService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/results")
public class ProcessingResultController {

    private final ProcessingResultService processingResultService;

    public ProcessingResultController(ProcessingResultService processingResultService) {
        this.processingResultService = processingResultService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ProcessingResultSummaryResponse> getSummary() {
        return ResponseEntity.ok(processingResultService.getSummary());
    }

    @GetMapping
    public ResponseEntity<List<ProcessingResultDetailResponse>> getResults(
            @RequestParam(required = false) Long pipelineHistoryId) {
        return ResponseEntity.ok(processingResultService.getAll(pipelineHistoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessingResultDetailResponse> getResult(@PathVariable Long id) {
        return ResponseEntity.ok(processingResultService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProcessingResultDetailResponse> createResult(
            @Valid @RequestBody ProcessingResultRequest request) {
        ProcessingResultDetailResponse created = processingResultService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessingResultDetailResponse> updateResult(@PathVariable Long id,
                                                                       @Valid @RequestBody ProcessingResultRequest request) {
        return ResponseEntity.ok(processingResultService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        processingResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

