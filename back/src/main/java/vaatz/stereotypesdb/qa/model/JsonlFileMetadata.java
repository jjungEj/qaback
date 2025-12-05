package vaatz.stereotypesdb.qa.model;

/**
* @ClassName	: JsonlFileMetadata.java
* @Description	: JSONL 변환 결과 파일의 메타데이터를 보관한다
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- JSONL 변환 결과 파일의 메타데이터를 보관
*/
public class JsonlFileMetadata {

    private final String fileName;
    private final String absolutePath;

    public JsonlFileMetadata(String fileName, String absolutePath) {
        this.fileName = fileName;
        this.absolutePath = absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}

