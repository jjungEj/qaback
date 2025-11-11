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
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.dto.ResultRequest;
import vaatz.stereotypesdb.qa.service.ResultService;

import jakarta.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping
    public ResponseEntity<List<Result>> getResults(@RequestParam(required = false) Long pipelineId) {
        if (pipelineId != null) {
            return ResponseEntity.ok(resultService.getByPipeline(pipelineId));
        }
        return ResponseEntity.ok(resultService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result> getResult(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Result> createResult(@Valid @RequestBody ResultRequest request) {
        Result created = resultService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> updateResult(@PathVariable Long id, @Valid @RequestBody ResultRequest request) {
        return ResponseEntity.ok(resultService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
