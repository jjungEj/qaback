package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ResultRequest {

    private String status;

    private String metrics;

    private String log;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @NotNull(message = "연결할 파이프라인 ID는 필수입니다.")
    private Long pipelineId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }
}
