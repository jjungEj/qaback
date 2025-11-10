package vaatz.stereotypesdb.qa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
	
	@GetMapping // 피드백 목록 조회
	public ResponseEntity<List<Map<String, Object>>> getFeedbacks(){
		List<Map<String, Object>> feedbacks = new ArrayList<>();
		return ResponseEntity.ok(feedbacks);
	}
	
	@PostMapping // 피드백 생성
	public ResponseEntity<Map<String, Object>> createFeedbackEntity (@RequestBody Map<String, Object> feedback) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Feedback updated successfully");
		response.put("feedback", feedback);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}") // 피드백 수정 
	public ResponseEntity<Map<String, Object>> updateFeedback(@PathVariable Long id, @RequestBody Map<String, Object> feedback) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Feedback updated successfully");
		response.put("id", id);
		response.put("feedback", feedback);
		return ResponseEntity.ok(response);
	}
	
	
}
