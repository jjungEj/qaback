package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PipelineRequest {

    @NotBlank(message = "파이프라인 이름은 비워둘 수 없습니다.")
    private String name;

    private String status;

    private String configuration;

    @NotNull(message = "연결할 모델 ID는 필수입니다.")
    private Long modelId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
}
