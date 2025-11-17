package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QaFileInfoResponse {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String feedback;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private List<QaFileSheetResponse> sheets;

    public QaFileInfoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<QaFileSheetResponse> getSheets() {
        return sheets;
    }

    public void setSheets(List<QaFileSheetResponse> sheets) {
        this.sheets = sheets;
    }

    public static class QaFileSheetResponse {
        private Long id;
        private String sheetName;
        private Integer sheetOrder;
        private String htmlContent;

        public QaFileSheetResponse() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public Integer getSheetOrder() {
            return sheetOrder;
        }

        public void setSheetOrder(Integer sheetOrder) {
            this.sheetOrder = sheetOrder;
        }

        public String getHtmlContent() {
            return htmlContent;
        }

        public void setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
        }
    }
}

