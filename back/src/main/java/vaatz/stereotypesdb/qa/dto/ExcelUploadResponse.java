package vaatz.stereotypesdb.qa.dto;

import java.util.List;

public class ExcelUploadResponse {
    private String fileName;
    private List<SheetHtmlResponse> sheets;

    public ExcelUploadResponse() {
    }

    public ExcelUploadResponse(String fileName, List<SheetHtmlResponse> sheets) {
        this.fileName = fileName;
        this.sheets = sheets;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<SheetHtmlResponse> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetHtmlResponse> sheets) {
        this.sheets = sheets;
    }

    public static class SheetHtmlResponse {
        private String sheetName;
        private String htmlContent;

        public SheetHtmlResponse() {
        }

        public SheetHtmlResponse(String sheetName, String htmlContent) {
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
    }
}

