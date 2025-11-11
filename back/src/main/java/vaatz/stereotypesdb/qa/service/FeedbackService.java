package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Feedback;
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.dto.FeedbackRequest;
import vaatz.stereotypesdb.qa.repository.FeedbackRepository;

import java.util.List;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ResultService resultService;

    public FeedbackService(FeedbackRepository feedbackRepository, ResultService resultService) {
        this.feedbackRepository = feedbackRepository;
        this.resultService = resultService;
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public Feedback getById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 피드백을 찾을 수 없습니다."));
    }

    public List<Feedback> getByResult(Long resultId) {
        return feedbackRepository.findByResultId(resultId);
    }

    public Feedback create(FeedbackRequest request) {
        Result result = resultService.getById(request.getResultId());
        Feedback feedback = new Feedback();
        feedback.setAuthor(request.getAuthor());
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedback.setResult(result);
        return feedbackRepository.save(feedback);
    }

    public Feedback update(Long id, FeedbackRequest request) {
        Feedback feedback = getById(id);
        Result result = resultService.getById(request.getResultId());
        feedback.setAuthor(request.getAuthor());
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedback.setResult(result);
        return feedbackRepository.save(feedback);
    }

    public void delete(Long id) {
        Feedback feedback = getById(id);
        feedbackRepository.delete(feedback);
    }
}
