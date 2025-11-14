package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class PipelineRequest {

    @NotBlank(message = "문서명은 필수입니다.")
    private String documentName;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String errorMessage;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

