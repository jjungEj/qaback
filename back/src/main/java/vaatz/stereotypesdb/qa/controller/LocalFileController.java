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
import vaatz.stereotypesdb.qa.dto.LocalFileDocumentRequest;
import vaatz.stereotypesdb.qa.dto.LocalFileDocumentResponse;
import vaatz.stereotypesdb.qa.dto.LocalFileQueueSummaryResponse;
import vaatz.stereotypesdb.qa.service.LocalFileDocumentService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/local-files")
public class LocalFileController {

    private final LocalFileDocumentService localFileDocumentService;

    public LocalFileController(LocalFileDocumentService localFileDocumentService) {
        this.localFileDocumentService = localFileDocumentService;
    }

    @GetMapping("/summary")
    public ResponseEntity<LocalFileQueueSummaryResponse> getSummary() {
        return ResponseEntity.ok(localFileDocumentService.getSummary());
    }

    @GetMapping
    public ResponseEntity<List<LocalFileDocumentResponse>> getFiles() {
        return ResponseEntity.ok(localFileDocumentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalFileDocumentResponse> getFile(@PathVariable Long id) {
        return ResponseEntity.ok(localFileDocumentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<LocalFileDocumentResponse> createFile(@Valid @RequestBody LocalFileDocumentRequest request) {
        LocalFileDocumentResponse created = localFileDocumentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalFileDocumentResponse> updateFile(@PathVariable Long id,
                                                                @Valid @RequestBody LocalFileDocumentRequest request) {
        return ResponseEntity.ok(localFileDocumentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        localFileDocumentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

