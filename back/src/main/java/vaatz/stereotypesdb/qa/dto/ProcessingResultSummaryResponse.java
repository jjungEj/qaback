package vaatz.stereotypesdb.qa.dto;

public class ProcessingResultSummaryResponse {

    private Long totalDocuments;
    private Long completedDocuments;
    private Long failedDocuments;

    public Long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(Long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public Long getCompletedDocuments() {
        return completedDocuments;
    }

    public void setCompletedDocuments(Long completedDocuments) {
        this.completedDocuments = completedDocuments;
    }

    public Long getFailedDocuments() {
        return failedDocuments;
    }

    public void setFailedDocuments(Long failedDocuments) {
        this.failedDocuments = failedDocuments;
    }
}

