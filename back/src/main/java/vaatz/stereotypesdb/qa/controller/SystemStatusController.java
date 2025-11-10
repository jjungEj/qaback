package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system-status")
public class SystemStatusController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("uptime", System.currentTimeMillis());
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }
}

