package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
* @ClassName	: HtmlUpdateRequest.java
* @Description	: HTML 업데이트 및 JSONL 변환 요청 DTO
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 편집된 HTML과 이미지를 JSONL로 변환하기 위한 요청 데이터
*/
public class HtmlUpdateRequest {
    @NotBlank(message = "파일명은 필수입니다.")
    private String fileName;
    
    private List<SheetHtmlUpdate> sheets;

    public HtmlUpdateRequest() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<SheetHtmlUpdate> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetHtmlUpdate> sheets) {
        this.sheets = sheets;
    }

    public static class SheetHtmlUpdate {
        @NotBlank(message = "시트명은 필수입니다.")
        private String sheetName;
        
        @NotBlank(message = "HTML 내용은 필수입니다.")
        private String htmlContent;
        
        private String imageBase64; // HTML 테이블을 이미지로 변환한 base64 인코딩 문자열

        public SheetHtmlUpdate() {
        }

        public SheetHtmlUpdate(String sheetName, String htmlContent) {
            this.sheetName = sheetName;
            this.htmlContent = htmlContent;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
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
}

