package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

public class SystemStatusSummaryResponse {

    private String overallStatus;
    private String aiDbStatus;
    private String helpyStatus;
    private Long uptimeSeconds;
    private Long totalDocuments;
    private Long documentsProcessedToday;
    private LocalDateTime lastCheckedAt;

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getAiDbStatus() {
        return aiDbStatus;
    }

    public void setAiDbStatus(String aiDbStatus) {
        this.aiDbStatus = aiDbStatus;
    }

    public String getHelpyStatus() {
        return helpyStatus;
    }

    public void setHelpyStatus(String helpyStatus) {
        this.helpyStatus = helpyStatus;
    }

    public Long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(Long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public Long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(Long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public Long getDocumentsProcessedToday() {
        return documentsProcessedToday;
    }

    public void setDocumentsProcessedToday(Long documentsProcessedToday) {
        this.documentsProcessedToday = documentsProcessedToday;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }
}

