package vaatz.stereotypesdb.qa.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
* @ClassName  : ResultPageResponse.java
* @Description: 결과 목록 페이징 응답 DTO
* @Author     : 정은주
* @Date       : 2025.12.04
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.12.04        정은주       - 페이지네이션 정보 및 이동 버튼 지원
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultPageResponse {
    private List<ResultSummaryResponse> contents = Collections.emptyList();
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasPrevious;
    private boolean hasNext;
    private List<Integer> navigationPages = Collections.emptyList();

    public List<ResultSummaryResponse> getContents() {
        return contents;
    }

    public void setContents(List<ResultSummaryResponse> contents) {
        this.contents = contents;
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

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<Integer> getNavigationPages() {
        return navigationPages;
    }

    public void setNavigationPages(List<Integer> navigationPages) {
        this.navigationPages = navigationPages;
    }
}
