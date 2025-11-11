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
import org.springframework.web.bind.annotation.RestController;
import vaatz.stereotypesdb.qa.domain.Model;
import vaatz.stereotypesdb.qa.dto.ModelRequest;
import vaatz.stereotypesdb.qa.service.ModelService;

import jakarta.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public ResponseEntity<List<Model>> getModels() {
        return ResponseEntity.ok(modelService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model> getModel(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Model> createModel(@Valid @RequestBody ModelRequest request) {
        Model created = modelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Model> updateModel(@PathVariable Long id, @Valid @RequestBody ModelRequest request) {
        return ResponseEntity.ok(modelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

