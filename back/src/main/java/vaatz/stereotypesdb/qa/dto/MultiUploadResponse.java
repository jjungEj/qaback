package vaatz.stereotypesdb.qa.dto;

import java.util.ArrayList;
import java.util.List;

/**
* @ClassName	: MultiUploadResponse.java
* @Description	: 다중 파일 업로드 응답 DTO
* @Author		: GPT-5.1 Codex
* @Date			: 2025.11.19
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.19        GPT-5.1 Codex      - 업로드/중복/실패 파일 구분 응답
*/
public class MultiUploadResponse {

    private int totalRequested;
    private List<QaFileInfoResponse> uploadedFiles = new ArrayList<>();
    private List<DuplicateFile> duplicateFiles = new ArrayList<>();
    private List<FailedFile> failedFiles = new ArrayList<>();

    public int getTotalRequested() {
        return totalRequested;
    }

    public void setTotalRequested(int totalRequested) {
        this.totalRequested = totalRequested;
    }

    public List<QaFileInfoResponse> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<QaFileInfoResponse> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public List<DuplicateFile> getDuplicateFiles() {
        return duplicateFiles;
    }

    public void setDuplicateFiles(List<DuplicateFile> duplicateFiles) {
        this.duplicateFiles = duplicateFiles;
    }

    public List<FailedFile> getFailedFiles() {
        return failedFiles;
    }

    public void setFailedFiles(List<FailedFile> failedFiles) {
        this.failedFiles = failedFiles;
    }

    public void addUploadedFile(QaFileInfoResponse response) {
        this.uploadedFiles.add(response);
    }

    public void addDuplicateFile(String fileName, String reason) {
        this.duplicateFiles.add(new DuplicateFile(fileName, reason));
    }

    public void addFailedFile(String fileName, String reason) {
        this.failedFiles.add(new FailedFile(fileName, reason));
    }

    public static class DuplicateFile {
        private String fileName;
        private String reason;

        public DuplicateFile() {
        }

        public DuplicateFile(String fileName, String reason) {
            this.fileName = fileName;
            this.reason = reason;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class FailedFile {
        private String fileName;
        private String reason;

        public FailedFile() {
        }

        public FailedFile(String fileName, String reason) {
            this.fileName = fileName;
            this.reason = reason;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
