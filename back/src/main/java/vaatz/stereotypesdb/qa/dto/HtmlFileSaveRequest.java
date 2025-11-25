package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

/**
* @ClassName	: HtmlFileSaveRequest.java
* @Description	: HTML 내용을 저장/덮어쓸 때 사용하는 요청 DTO.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- before 폴더의 HTML 파일 내용 저장 요청 DTO
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

