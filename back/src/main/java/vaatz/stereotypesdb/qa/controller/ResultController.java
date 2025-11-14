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
import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultRequest;
import vaatz.stereotypesdb.qa.dto.ResultSummaryResponse;
import vaatz.stereotypesdb.qa.service.ResultService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ResultSummaryResponse> getSummary() {
        return ResponseEntity.ok(resultService.getSummary());
    }

    @GetMapping
    public ResponseEntity<List<ResultDetailResponse>> getResults(
            @RequestParam(required = false) Long pipelineId) {
        return ResponseEntity.ok(resultService.getAll(pipelineId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDetailResponse> getResult(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ResultDetailResponse> createResult(
            @Valid @RequestBody ResultRequest request) {
        ResultDetailResponse created = resultService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDetailResponse> updateResult(@PathVariable Long id,
                                                             @Valid @RequestBody ResultRequest request) {
        return ResponseEntity.ok(resultService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

