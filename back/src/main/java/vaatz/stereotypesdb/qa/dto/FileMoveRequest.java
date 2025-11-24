package vaatz.stereotypesdb.qa.dto;

import javax.validation.constraints.NotBlank;

/**
 * 파일 이동 요청 DTO (after -> dev).
 */
public class FileMoveRequest {

    @NotBlank(message = "fileName은 필수입니다.")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

