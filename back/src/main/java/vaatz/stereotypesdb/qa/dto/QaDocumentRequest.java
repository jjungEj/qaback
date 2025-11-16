package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

public class QaDocumentRequest {

    @NotBlank(message = "문서명은 필수입니다.")
    private String documentName;

    private String documentType;

    private String qaContent;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;

    private Long resultId;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getQaContent() {
        return qaContent;
    }

    public void setQaContent(String qaContent) {
        this.qaContent = qaContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }
}

