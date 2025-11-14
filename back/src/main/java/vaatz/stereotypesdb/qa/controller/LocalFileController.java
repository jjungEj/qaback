package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vaatz.stereotypesdb.qa.dto.LocalFileRequest;
import vaatz.stereotypesdb.qa.dto.LocalFileResponse;
import vaatz.stereotypesdb.qa.dto.LocalFileSummaryResponse;
import vaatz.stereotypesdb.qa.service.LocalFileService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/local-files")
public class LocalFileController {

    private final LocalFileService localFileService;

    public LocalFileController(LocalFileService localFileService) {
        this.localFileService = localFileService;
    }

    @GetMapping("/summary")
    public ResponseEntity<LocalFileSummaryResponse> getSummary() {
        return ResponseEntity.ok(localFileService.getSummary());
    }

    @GetMapping
    public ResponseEntity<List<LocalFileResponse>> getFiles() {
        return ResponseEntity.ok(localFileService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalFileResponse> getFile(@PathVariable Long id) {
        return ResponseEntity.ok(localFileService.getById(id));
    }

    @PostMapping
    public ResponseEntity<LocalFileResponse> createFile(@Valid @RequestBody LocalFileRequest request) {
        LocalFileResponse created = localFileService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LocalFileResponse> uploadFile(@RequestPart("file") MultipartFile file) {
        LocalFileResponse created = localFileService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalFileResponse> updateFile(@PathVariable Long id,
                                                        @Valid @RequestBody LocalFileRequest request) {
        return ResponseEntity.ok(localFileService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        localFileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

