package vaatz.stereotypesdb.qa.dto;

import java.time.LocalDateTime;

/**
* @ClassName	: WorkspaceFileContentResponse.java
* @Description	: 파일 내용과 메타데이터를 함께 반환한다.
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- 파일 내용 및 메타데이터(폴더, 파일명, 크기, 수정일시) 응답 DTO
*/
public class WorkspaceFileContentResponse {
    private String folder;
    private String fileName;
    private long fileSize;
    private LocalDateTime lastModifiedAt;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

