package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultDetailResponse {

    private Long id;
    private String documentName;
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

    private List<ResultSheetResponse> sheets = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ResultSheetResponse> getSheets() {
        return sheets;
    }

    public void setSheets(List<ResultSheetResponse> sheets) {
        this.sheets = sheets;
    }
}

