package vaatz.stereotypesdb.qa.model;

import java.time.LocalDateTime;

/**
* @ClassName  : ResultItem.java
* @Description: DB 결과 테이블을 대체하는 임시 도메인 모델
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
*/
public class ResultItem {
    private final Long id;
    private final String title;
    private final String fileName;
    private final String status;
    private final LocalDateTime createdAt;

    public ResultItem(Long id, String title, String fileName, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
