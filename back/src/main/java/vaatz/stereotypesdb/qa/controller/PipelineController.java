package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPipelines() {
        List<Map<String, Object>> pipelines = new ArrayList<>();
        // TODO: 실제 파이프라인 데이터 조회 로직 구현
        return ResponseEntity.ok(pipelines);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPipeline(@RequestBody Map<String, Object> pipeline) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pipeline created successfully");
        response.put("pipeline", pipeline);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePipeline(@PathVariable Long id, @RequestBody Map<String, Object> pipeline) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pipeline updated successfully");
        response.put("id", id);
        response.put("pipeline", pipeline);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePipeline(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pipeline deleted successfully");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}

