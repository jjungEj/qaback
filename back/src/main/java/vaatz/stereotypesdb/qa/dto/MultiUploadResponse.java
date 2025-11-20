package vaatz.stereotypesdb.qa.dto;

import java.util.List;

/**
* @ClassName	: MultiUploadResponse.java
* @Description	: 다중 파일 업로드 응답 DTO
* @Author		: 정은주
* @Date			: 2025.11.18
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.18        정은주       	- 여러 파일 업로드 결과 응답
*/
public class MultiUploadResponse {
    private List<QaFileInfoResponse> successFiles;
    private List<DuplicateFileInfo> duplicateFiles;
    private List<ErrorFileInfo> errorFiles;

    public MultiUploadResponse() {
    }

    public List<QaFileInfoResponse> getSuccessFiles() {
        return successFiles;
    }

    public void setSuccessFiles(List<QaFileInfoResponse> successFiles) {
        this.successFiles = successFiles;
    }

    public List<DuplicateFileInfo> getDuplicateFiles() {
        return duplicateFiles;
    }

    public void setDuplicateFiles(List<DuplicateFileInfo> duplicateFiles) {
        this.duplicateFiles = duplicateFiles;
    }

    public List<ErrorFileInfo> getErrorFiles() {
        return errorFiles;
    }

    public void setErrorFiles(List<ErrorFileInfo> errorFiles) {
        this.errorFiles = errorFiles;
    }

    public static class DuplicateFileInfo {
        private String fileName;
        private String message;

        public DuplicateFileInfo() {
        }

        public DuplicateFileInfo(String fileName, String message) {
            this.fileName = fileName;
            this.message = message;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ErrorFileInfo {
        private String fileName;
        private String errorMessage;

        public ErrorFileInfo() {
        }

        public ErrorFileInfo(String fileName, String errorMessage) {
            this.fileName = fileName;
            this.errorMessage = errorMessage;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}

