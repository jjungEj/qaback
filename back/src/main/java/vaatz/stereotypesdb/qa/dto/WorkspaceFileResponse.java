package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

/**
* @ClassName	: WorkspaceFileResponse.java
* @Description	: 워크스페이스 내 단일 파일 정보를 나타낸다.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- 파일명, 크기, 수정일시, 확장자, 절대경로 정보 응답 DTO
*/
public class WorkspaceFileResponse {
    private String folder;
    private String fileName;
    private long fileSize;
    private LocalDateTime lastModifiedAt;
    private String extension;
    private String absolutePath;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}

