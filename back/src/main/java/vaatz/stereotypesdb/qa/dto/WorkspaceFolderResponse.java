package vaatz.stereotypesdb.qa.dto;

import java.util.List;

/**
 * 워크스페이스 폴더 단위 응답 DTO.
 */
public class WorkspaceFolderResponse {
    private String folder;
    private String displayName;
    private String absolutePath;
    private List<WorkspaceFileResponse> files;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public List<WorkspaceFileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<WorkspaceFileResponse> files) {
        this.files = files;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}

