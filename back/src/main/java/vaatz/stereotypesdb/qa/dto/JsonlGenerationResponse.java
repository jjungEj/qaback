package vaatz.stereotypesdb.qa.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * JSONL 변환 API 응답 DTO.
 */
public class JsonlGenerationResponse {

    private List<FileEntry> files = new ArrayList<>();

    public JsonlGenerationResponse() {
    }

    public JsonlGenerationResponse(List<FileEntry> files) {
        this.files = files;
    }

    public List<FileEntry> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntry> files) {
        this.files = files;
    }

    public static class FileEntry {
        private String fileName;
        private String path;

        public FileEntry() {
        }

        public FileEntry(String fileName, String path) {
            this.fileName = fileName;
            this.path = path;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}

