package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
* @ClassName	: HtmlUpdateRequest.java
* @Description	: HTML 업데이트 및 JSONL 변환 요청 DTO
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- 편집된 HTML과 이미지를 JSONL로 변환하기 위한 요청 데이터
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
        
        /**
         * HTML 테이블을 이미지로 변환한 base64 인코딩 문자열 (단일 테이블용)
         * 
         * 사용 시나리오:
         * - 테이블이 1개인 경우: 이 필드 사용
         * - 테이블이 2개 이상인 경우: imageBase64List가 없을 때만 사용 (하위 호환성)
         *                            imageBase64List가 있으면 무시됨
         */
        private String imageBase64;
        
        /**
         * 각 테이블별 이미지 리스트 (여러 테이블용)
         * 
         * 사용 시나리오:
         * - 테이블이 2개 이상인 경우: 각 테이블별로 별도의 이미지를 제공할 때 사용
         * - 리스트의 인덱스 순서대로 각 테이블에 매핑됨
         *   예: ["이미지1", "이미지2", "이미지3"] -> 첫 번째 테이블에 이미지1, 두 번째 테이블에 이미지2, ...
         * 
         * null이거나 크기가 부족한 경우:
         * - imageBase64 필드를 모든 테이블에 공유하여 사용 (하위 호환성)
         */
        private List<String> imageBase64List;

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

        public List<String> getImageBase64List() {
            return imageBase64List;
        }

        public void setImageBase64List(List<String> imageBase64List) {
            this.imageBase64List = imageBase64List;
        }
    }
}

