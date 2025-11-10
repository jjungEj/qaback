package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getResults() {
        List<Map<String, Object>> results = new ArrayList<>();
        // TODO: 실제 결과 데이터 조회 로직 구현
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getResult(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("message", "Result retrieved successfully");
        // TODO: 실제 결과 데이터 조회 로직 구현
        return ResponseEntity.ok(result);
    }
}

