package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ProcessingResultRequest {

    @NotNull(message = "연결할 파이프라인 기록 ID는 필수입니다.")
    private Long pipelineHistoryId;

    @NotBlank(message = "문서명은 필수입니다.")
    private String documentName;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;

    private Long actualFileSize;

    private Long transferredFileSize;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String originalFileName;

    private Long originalFileSize;

    private Long convertedFileSize;

    private String originalViewerUri;

    private String htmlRenderUri;

    private String metadata;

    public Long getPipelineHistoryId() {
        return pipelineHistoryId;
    }

    public void setPipelineHistoryId(Long pipelineHistoryId) {
        this.pipelineHistoryId = pipelineHistoryId;
    }

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

    public Long getActualFileSize() {
        return actualFileSize;
    }

    public void setActualFileSize(Long actualFileSize) {
        this.actualFileSize = actualFileSize;
    }

    public Long getTransferredFileSize() {
        return transferredFileSize;
    }

    public void setTransferredFileSize(Long transferredFileSize) {
        this.transferredFileSize = transferredFileSize;
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

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getOriginalFileSize() {
        return originalFileSize;
    }

    public void setOriginalFileSize(Long originalFileSize) {
        this.originalFileSize = originalFileSize;
    }

    public Long getConvertedFileSize() {
        return convertedFileSize;
    }

    public void setConvertedFileSize(Long convertedFileSize) {
        this.convertedFileSize = convertedFileSize;
    }

    public String getOriginalViewerUri() {
        return originalViewerUri;
    }

    public void setOriginalViewerUri(String originalViewerUri) {
        this.originalViewerUri = originalViewerUri;
    }

    public String getHtmlRenderUri() {
        return htmlRenderUri;
    }

    public void setHtmlRenderUri(String htmlRenderUri) {
        this.htmlRenderUri = htmlRenderUri;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}

