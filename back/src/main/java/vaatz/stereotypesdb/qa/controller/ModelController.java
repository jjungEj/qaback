package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getModels() {
        List<Map<String, Object>> models = new ArrayList<>();
        // TODO: 실제 모델 데이터 조회 로직 구현
        return ResponseEntity.ok(models);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createModel(@RequestBody Map<String, Object> model) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Model created successfully");
        response.put("model", model);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateModel(@PathVariable Long id, @RequestBody Map<String, Object> model) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Model updated successfully");
        response.put("id", id);
        response.put("model", model);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteModel(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Model deleted successfully");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}

