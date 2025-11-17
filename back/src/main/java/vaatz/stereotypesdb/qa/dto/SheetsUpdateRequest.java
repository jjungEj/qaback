package vaatz.stereotypesdb.qa.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class SheetsUpdateRequest {
    
    @NotEmpty(message = "시트 목록은 필수입니다.")
    @Valid
    private List<SheetUpdate> sheets;

    public SheetsUpdateRequest() {
    }

    public List<SheetUpdate> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetUpdate> sheets) {
        this.sheets = sheets;
    }

    public static class SheetUpdate {
        private Long id;
        
        private String htmlContent;

        public SheetUpdate() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getHtmlContent() {
            return htmlContent;
        }

        public void setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
        }
    }
}

