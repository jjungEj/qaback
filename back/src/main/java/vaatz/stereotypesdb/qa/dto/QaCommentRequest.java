package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

public class QaCommentRequest {

    @NotBlank(message = "코멘트는 필수입니다.")
    private String comment;

    private String modifiedField;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getModifiedField() {
        return modifiedField;
    }

    public void setModifiedField(String modifiedField) {
        this.modifiedField = modifiedField;
    }
}

