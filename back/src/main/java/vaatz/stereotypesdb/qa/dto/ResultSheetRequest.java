package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

public class ResultSheetRequest {

    @NotBlank(message = "시트명은 필수입니다.")
    private String sheetName;

    private Integer sheetOrder;

    private String htmlContent;

    private String imageBase64;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getSheetOrder() {
        return sheetOrder;
    }

    public void setSheetOrder(Integer sheetOrder) {
        this.sheetOrder = sheetOrder;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}

