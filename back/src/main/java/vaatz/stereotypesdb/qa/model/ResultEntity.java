package vaatz.stereotypesdb.qa.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* @ClassName  : ResultEntity.java
* @Description: 결과 테이블 매핑 엔티티 (DB 연동용)
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
*/
@Entity
@Table(name = "qa_result")
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(length = 30)
    private String status;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "text")
    private String documentImageBase64;

    @Column(columnDefinition = "text")
    private String htmlTableContent;

    @Column(columnDefinition = "text")
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
