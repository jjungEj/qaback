package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

/**
 * HTML 내용을 저장/덮어쓸 때 사용하는 요청 DTO.
 */
public class HtmlFileSaveRequest {

    @NotBlank(message = "htmlContent는 필수입니다.")
    private String htmlContent;

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}

