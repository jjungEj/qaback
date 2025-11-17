package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

public class FeedbackRequest {
    @NotBlank(message = "피드백 내용은 필수입니다.")
    private String feedback;

    public FeedbackRequest() {
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

