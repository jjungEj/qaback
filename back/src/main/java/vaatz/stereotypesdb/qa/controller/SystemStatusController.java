package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vaatz.stereotypesdb.qa.domain.SystemStatusSnapshot;
import vaatz.stereotypesdb.qa.dto.SystemStatusSnapshotRequest;
import vaatz.stereotypesdb.qa.dto.SystemStatusSummaryResponse;
import vaatz.stereotypesdb.qa.service.SystemStatusService;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/system-status")
public class SystemStatusController {

    private final SystemStatusService systemStatusService;

    public SystemStatusController(SystemStatusService systemStatusService) {
        this.systemStatusService = systemStatusService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SystemStatusSummaryResponse> getSummary() {
        return ResponseEntity.ok(systemStatusService.getSummary());
    }

    @PostMapping("/snapshots")
    public ResponseEntity<SystemStatusSnapshot> createSnapshot(@Valid @RequestBody SystemStatusSnapshotRequest request) {
        SystemStatusSnapshot created = systemStatusService.createSnapshot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

