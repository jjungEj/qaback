package vaatz.stereotypesdb.qa.model;

/**
 * JSONL 변환 시 사용하는 HTML/이미지 페이로드 모델.
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

