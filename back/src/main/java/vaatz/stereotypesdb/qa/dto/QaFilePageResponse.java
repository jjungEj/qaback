package vaatz.stereotypesdb.qa.dto;

import java.util.List;

/**
* @ClassName	: QaFilePageResponse.java
* @Description	: QA 파일 목록 페이지 응답 DTO
* @Author		: GPT-5.1 Codex
* @Date			: 2025.11.19
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.19        GPT-5.1 Codex      - 페이지네이션 메타데이터 포함 DTO
*/
public class QaFilePageResponse {

    private List<QaFileInfoResponse> files;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;

    public List<QaFileInfoResponse> getFiles() {
        return files;
    }

    public void setFiles(List<QaFileInfoResponse> files) {
        this.files = files;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
