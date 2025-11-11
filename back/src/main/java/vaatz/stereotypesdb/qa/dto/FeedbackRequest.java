package vaatz.stereotypesdb.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FeedbackRequest {

    @NotNull(message = "연결할 결과 ID는 필수입니다.")
    private Long resultId;

    @NotBlank(message = "작성자 이름은 비워둘 수 없습니다.")
    private String author;

    @NotBlank(message = "피드백 내용은 비워둘 수 없습니다.")
    @Size(max = 500, message = "피드백은 최대 500자까지 입력할 수 있습니다.")
    private String comment;

    private Integer rating;

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
