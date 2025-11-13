package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FeedbackEntryRequest {

    @NotNull(message = "연결할 결과 ID는 필수입니다.")
    private Long processingResultId;

    @NotBlank(message = "문서명은 필수입니다.")
    private String documentName;

    @NotBlank(message = "로그 타입은 필수입니다.")
    private String logType;

    @NotBlank(message = "피드백 내용은 필수입니다.")
    private String feedback;

    private String status;

    private String updatedResultStatus;

    private String updatedMetadata;

    public Long getProcessingResultId() {
        return processingResultId;
    }

    public void setProcessingResultId(Long processingResultId) {
        this.processingResultId = processingResultId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedResultStatus() {
        return updatedResultStatus;
    }

    public void setUpdatedResultStatus(String updatedResultStatus) {
        this.updatedResultStatus = updatedResultStatus;
    }

    public String getUpdatedMetadata() {
        return updatedMetadata;
    }

    public void setUpdatedMetadata(String updatedMetadata) {
        this.updatedMetadata = updatedMetadata;
    }
}

