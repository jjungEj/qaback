package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
* @ClassName  : ResultSummaryResponse.java
* @Description: 결과 목록 카드 하나에 필요한 핵심 정보 DTO
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.12.04        GPT-5.1 Codex      - mock 데이터 기반 목록 응답 생성
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultSummaryResponse {
    private Long id;
    private String title;
    private String fileName;
    private String status;
    private LocalDateTime createdAt;

    public ResultSummaryResponse() {
    }

    public ResultSummaryResponse(Long id, String title, String fileName, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.status = status;
        this.createdAt = createdAt;
    }

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
}
