package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
* @ClassName  : ResultSummaryResponse.java
* @Description: 결과 목록 카드 하나에 필요한 핵심 정보 DTO
* @Author     : 정은주
* @Date       : 2025.12.04
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.12.04        정은주      - 파일 기반 응답 구조 정의
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultSummaryResponse {
    private String fileName;
    private String folder;
    private String extension;
    private long fileSize;
    private LocalDateTime lastModifiedAt;
    @JsonProperty("completed")
    private boolean isCompleted; // dev 폴더에 파일이 있으면 완료 상태

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
