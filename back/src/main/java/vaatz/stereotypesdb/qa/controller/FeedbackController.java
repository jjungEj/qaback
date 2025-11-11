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
import vaatz.stereotypesdb.qa.domain.Feedback;
import vaatz.stereotypesdb.qa.dto.FeedbackRequest;
import vaatz.stereotypesdb.qa.service.FeedbackService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getFeedbacks(@RequestParam(required = false) Long resultId) {
        if (resultId != null) {
            return ResponseEntity.ok(feedbackService.getByResult(resultId));
        }
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedback(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        Feedback created = feedbackService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
