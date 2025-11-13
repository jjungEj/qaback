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
import vaatz.stereotypesdb.qa.dto.FeedbackEntryRequest;
import vaatz.stereotypesdb.qa.dto.FeedbackEntryResponse;
import vaatz.stereotypesdb.qa.service.FeedbackEntryService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/feedback")
public class FeedbackEntryController {

    private final FeedbackEntryService feedbackEntryService;

    public FeedbackEntryController(FeedbackEntryService feedbackEntryService) {
        this.feedbackEntryService = feedbackEntryService;
    }

    @GetMapping
    public ResponseEntity<List<FeedbackEntryResponse>> getFeedback(
            @RequestParam(required = false) Long processingResultId) {
        return ResponseEntity.ok(feedbackEntryService.getAll(processingResultId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackEntryResponse> getFeedback(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackEntryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FeedbackEntryResponse> createFeedback(@Valid @RequestBody FeedbackEntryRequest request) {
        FeedbackEntryResponse created = feedbackEntryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackEntryResponse> updateFeedback(@PathVariable Long id,
                                                                @Valid @RequestBody FeedbackEntryRequest request) {
        return ResponseEntity.ok(feedbackEntryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

