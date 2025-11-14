package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class SystemStatusRequest {

    @NotBlank(message = "AI DB 상태는 필수입니다.")
    private String aiDbStatus;

    @NotBlank(message = "Helpy 상태는 필수입니다.")
    private String helpyStatus;

    @NotBlank(message = "전체 시스템 상태는 필수입니다.")
    private String overallStatus;

    private LocalDateTime uptimeSince;

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

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public LocalDateTime getUptimeSince() {
        return uptimeSince;
    }

    public void setUptimeSince(LocalDateTime uptimeSince) {
        this.uptimeSince = uptimeSince;
    }
}

