package vaatz.stereotypesdb.qa.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultPageResponse;
import vaatz.stereotypesdb.qa.dto.ResultSummaryResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileContentResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFolderResponse;
import vaatz.stereotypesdb.qa.model.WorkspaceFolderType;

/**
* @ClassName  : ResultService.java
* @Description: 결과 조회(목록/상세) 및 QA 전달 준비 로직 - 파일 기반
* @Author     : 정은주
* @Date       : 2025.12.04
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.12.04        정은주       	- 결과 조회(목록/상세) 및 QA 전달 준비 로직 - 파일 기반
*/
@Service
public class ResultService {

    private static final int NAVIGATION_WINDOW = 5;

    private final FileWorkspaceService fileWorkspaceService;

    public ResultService(FileWorkspaceService fileWorkspaceService) {
        this.fileWorkspaceService = fileWorkspaceService;
    }

    public ResultPageResponse getResultPage(int page, int size) {
        // before와 after 폴더의 파일을 모두 조회
        WorkspaceFolderResponse beforePage = fileWorkspaceService.getFolderPage(
                WorkspaceFolderType.BEFORE, 0, Integer.MAX_VALUE, null);
        WorkspaceFolderResponse afterPage = fileWorkspaceService.getFolderPage(
                WorkspaceFolderType.AFTER, 0, Integer.MAX_VALUE, null);

        // 두 폴더의 파일을 합치고 수정일시 기준 내림차순 정렬
        List<ResultSummaryResponse> allSummaries = new ArrayList<>();
        allSummaries.addAll(beforePage.getFiles().stream()
                .map(this::toSummary)
                .collect(Collectors.toList()));
        allSummaries.addAll(afterPage.getFiles().stream()
                .map(this::toSummary)
                .collect(Collectors.toList()));
        
        // 수정일시 기준 내림차순 정렬
        allSummaries.sort((a, b) -> {
            if (a.getLastModifiedAt() == null && b.getLastModifiedAt() == null) {
                return 0;
            }
            if (a.getLastModifiedAt() == null) {
                return 1;
            }
            if (b.getLastModifiedAt() == null) {
                return -1;
            }
            return b.getLastModifiedAt().compareTo(a.getLastModifiedAt());
        });

        // 페이징 처리
        int totalElements = allSummaries.size();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        int safePage = totalPages == 0 ? 0 : Math.min(page, totalPages - 1);
        int fromIndex = totalElements == 0 ? 0 : safePage * size;
        int toIndex = totalElements == 0 ? 0 : Math.min(fromIndex + size, totalElements);
        List<ResultSummaryResponse> pageContent = totalElements == 0
                ? Collections.emptyList()
                : new ArrayList<>(allSummaries.subList(fromIndex, toIndex));

        ResultPageResponse response = new ResultPageResponse();
        response.setContents(pageContent);
        response.setPage(safePage);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setHasPrevious(safePage > 0);
        response.setHasNext(safePage < totalPages - 1);
        response.setNavigationPages(buildNavigation(safePage, totalPages));
        return response;
    }

    public ResultDetailResponse getResultDetail(String fileName) {
        WorkspaceFileContentResponse content = fileWorkspaceService.readFile(WorkspaceFolderType.BEFORE, fileName);
        ResultDetailResponse response = new ResultDetailResponse();
        response.setFileName(content.getFileName());
        response.setFolder(content.getFolder());
        response.setFileSize(content.getFileSize());
        response.setLastModifiedAt(content.getLastModifiedAt());
        response.setDocumentImageBase64(null);
        response.setHtmlTableContent(null);
        response.setExtractionSummary(null);
        return response;
    }

    public String resolveQaHtmlContent(String fileName) {
        WorkspaceFileContentResponse content = fileWorkspaceService.readFile(WorkspaceFolderType.BEFORE, fileName);
        if (content.getContent() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일 내용을 찾을 수 없습니다.");
        }
        return content.getContent();
    }

    private ResultSummaryResponse toSummary(WorkspaceFileResponse file) {
        ResultSummaryResponse summary = new ResultSummaryResponse();
        summary.setFileName(file.getFileName());
        summary.setFolder(file.getFolder());
        summary.setExtension(file.getExtension());
        summary.setFileSize(file.getFileSize());
        summary.setLastModifiedAt(file.getLastModifiedAt());
        summary.setCompleted(file.isCompleted()); // 완료 상태 복사
        return summary;
    }

    private List<Integer> buildNavigation(int currentPage, int totalPages) {
        if (totalPages <= 0) {
            return Collections.emptyList();
        }
        int window = Math.min(NAVIGATION_WINDOW, totalPages);
        int start = Math.max(0, currentPage - NAVIGATION_WINDOW / 2);
        int end = Math.min(totalPages, start + window);
        if (end - start < window) {
            start = Math.max(0, end - window);
        }
        List<Integer> pages = new ArrayList<>();
        for (int page = start; page < end; page++) {
            pages.add(page + 1); // 프론트 표기는 1부터 시작
        }
        return pages;
    }
}
