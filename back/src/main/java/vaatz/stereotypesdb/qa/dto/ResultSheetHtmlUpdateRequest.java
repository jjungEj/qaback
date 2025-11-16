package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

public class ResultSheetHtmlUpdateRequest {

    @NotBlank(message = "HTML 내용은 필수입니다.")
    private String htmlContent;

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}

