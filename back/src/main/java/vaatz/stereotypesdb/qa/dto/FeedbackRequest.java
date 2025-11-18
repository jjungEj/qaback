package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

/**
* @ClassName	: FeedbackRequest.java
* @Description	: 피드백 저장 요청 DTO
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 파일에 대한 피드백 내용 저장 요청
*/
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

