package vaatz.stereotypesdb.qa.dto;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * JSON predict 변환 결과 응답 DTO.
 */
public class JsonPredictConversionResponse {

    private String jsonFileName;
    private String jsonFileBase64;
    private String jsonlFileName;
    private String jsonlFileBase64;
    private List<RecordSummary> records = new ArrayList<>();

    public String getJsonFileName() {
        return jsonFileName;
    }

    public void setJsonFileName(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    public String getJsonFileBase64() {
        return jsonFileBase64;
    }

    public void setJsonFileBase64(byte[] jsonBytes) {
        this.jsonFileBase64 = encode(jsonBytes);
    }

    public String getJsonlFileName() {
        return jsonlFileName;
    }

    public void setJsonlFileName(String jsonlFileName) {
        this.jsonlFileName = jsonlFileName;
    }

    public String getJsonlFileBase64() {
        return jsonlFileBase64;
    }

    public void setJsonlFileBase64(byte[] jsonlBytes) {
        this.jsonlFileBase64 = encode(jsonlBytes);
    }

    public List<RecordSummary> getRecords() {
        return records;
    }

    public void setRecords(List<RecordSummary> records) {
        this.records = records;
    }

    private String encode(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(payload);
    }

    public static class RecordSummary {
        private String identifier;
        private String imagePath;
        private String predictHtml;

        public RecordSummary() {
        }

        public RecordSummary(String identifier, String imagePath, String predictHtml) {
            this.identifier = identifier;
            this.imagePath = imagePath;
            this.predictHtml = predictHtml;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getPredictHtml() {
            return predictHtml;
        }

        public void setPredictHtml(String predictHtml) {
            this.predictHtml = predictHtml;
        }
    }
}
