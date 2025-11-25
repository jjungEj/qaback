package vaatz.stereotypesdb.qa.model;

/**
* @ClassName	: HtmlSheetData.java
* @Description	: JSONL 변환 시 사용하는 HTML/이미지 페이로드 모델.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- 시트명, HTML 내용, 이미지 base64 데이터 모델
*/
public class HtmlSheetData {
    private final String sheetName;
    private final String htmlContent;
    private final String imageBase64;

    public HtmlSheetData(String sheetName, String htmlContent, String imageBase64) {
        this.sheetName = sheetName;
        this.htmlContent = htmlContent;
        this.imageBase64 = imageBase64;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getImageBase64() {
        return imageBase64;
    }
}

