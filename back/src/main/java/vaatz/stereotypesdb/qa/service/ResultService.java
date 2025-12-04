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
*/
@Service
public class ResultService {

    private static final int NAVIGATION_WINDOW = 5;

    private final FileWorkspaceService fileWorkspaceService;

    public ResultService(FileWorkspaceService fileWorkspaceService) {
        this.fileWorkspaceService = fileWorkspaceService;
    }

    public ResultPageResponse getResultPage(int page, int size) {
        WorkspaceFolderResponse folderPage = fileWorkspaceService.getFolderPage(
                WorkspaceFolderType.BEFORE, page, size, null);

        ResultPageResponse response = new ResultPageResponse();
        List<ResultSummaryResponse> summaries = folderPage.getFiles().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        response.setContents(summaries);
        response.setPage(folderPage.getPage());
        response.setSize(folderPage.getSize());
        response.setTotalElements(folderPage.getTotalElements());
        response.setTotalPages(folderPage.getTotalPages());
        response.setHasPrevious(folderPage.getPage() > 0);
        response.setHasNext(folderPage.getPage() < folderPage.getTotalPages() - 1);
        response.setNavigationPages(buildNavigation(folderPage.getPage(), folderPage.getTotalPages()));
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
