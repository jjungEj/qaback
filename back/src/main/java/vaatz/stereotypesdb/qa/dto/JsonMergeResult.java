package vaatz.stereotypesdb.qa.dto;

/**
* @ClassName	: JsonMergeResult.java
* @Description	: HTML 병합 후 생성된 JSON 파일 정보와 바이너리를 담는다.
* @Author		: GPT-5.1 Codex
* @Date			: 2025.11.28
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.28        GPT-5.1 Codex     - after 저장 메타데이터 및 파일명 제공
*/

public class JsonMergeResult {

    private String fileName;
    private byte[] jsonBytes;
    private WorkspaceFileResponse savedFile;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getJsonBytes() {
        return jsonBytes;
    }

    public void setJsonBytes(byte[] jsonBytes) {
        this.jsonBytes = jsonBytes;
    }

    public WorkspaceFileResponse getSavedFile() {
        return savedFile;
    }

    public void setSavedFile(WorkspaceFileResponse savedFile) {
        this.savedFile = savedFile;
    }
}

