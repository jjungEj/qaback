package vaatz.stereotypesdb.qa.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

/**
* @ClassName	: JsonMergeRequest.java
* @Description	: HTML 파일을 병합해 JSON을 생성할 때 사용하는 요청 DTO.
* @Author		: GPT-5.1 Codex
* @Date			: 2025.11.28
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.28        GPT-5.1 Codex     - 병합 대상 HTML 목록과 결과 파일명을 전달
*/
public class JsonMergeRequest {

    @NotEmpty(message = "병합할 HTML 파일명이 최소 1개 이상 필요합니다.")
    private List<String> fileNames;

    /**
     * after 폴더에 저장할 출력 JSON 파일명 (확장자 없이 전달 가능).
     */
    private String outputFileName;

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}

