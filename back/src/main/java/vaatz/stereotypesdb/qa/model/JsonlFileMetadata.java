package vaatz.stereotypesdb.qa.model;

/**
 * JSONL 변환 결과 파일의 메타데이터를 보관한다.
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

