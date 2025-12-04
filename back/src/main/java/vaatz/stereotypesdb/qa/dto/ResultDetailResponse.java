package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
* @ClassName  : ResultDetailResponse.java
* @Description: 결과 상세 화면에 필요한 데이터 DTO
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.12.04        GPT-5.1 Codex      - 문단 3개에 대응하는 필드 정의
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDetailResponse {
    private Long id;
    private String title;
    private String fileName;
    private String status;
    private LocalDateTime createdAt;

    // 문단 3개 - 현재는 DB 미구현이라 빈칸 유지
    private String documentImageBase64;
    private String htmlTableContent;
    private String extractionSummary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
