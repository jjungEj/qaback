package vaatz.stereotypesdb.qa.dto;

public class LocalFileQueueSummaryResponse {

    private Long totalDocuments;
    private Long pendingDocuments;
    private Long completedDocuments;

    public Long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(Long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public Long getPendingDocuments() {
        return pendingDocuments;
    }

    public void setPendingDocuments(Long pendingDocuments) {
        this.pendingDocuments = pendingDocuments;
    }

    public Long getCompletedDocuments() {
        return completedDocuments;
    }

    public void setCompletedDocuments(Long completedDocuments) {
        this.completedDocuments = completedDocuments;
    }
}

