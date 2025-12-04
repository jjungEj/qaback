package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
* @ClassName  : ResultDetailResponse.java
* @Description: 결과 상세 화면에 필요한 데이터 DTO
* @Author     : 정은주
* @Date       : 2025.12.04
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.12.04        정은주      - 파일 기반 상세 응답 구조 정의
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDetailResponse {
    private String fileName;
    private String folder;
    private long fileSize;
    private LocalDateTime lastModifiedAt;

    // 문단 3개 - 현재는 DB 미구현이라 빈칸 유지
    private String documentImageBase64;
    private String htmlTableContent;
    private String extractionSummary;

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

    public String getDocumentImageBase64() {
        return documentImageBase64;
    }

    public void setDocumentImageBase64(String documentImageBase64) {
        this.documentImageBase64 = documentImageBase64;
    }

    public String getHtmlTableContent() {
        return htmlTableContent;
    }

    public void setHtmlTableContent(String htmlTableContent) {
        this.htmlTableContent = htmlTableContent;
    }

    public String getExtractionSummary() {
        return extractionSummary;
    }

    public void setExtractionSummary(String extractionSummary) {
        this.extractionSummary = extractionSummary;
    }
}
