package vaatz.stereotypesdb.qa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultPageResponse;
import vaatz.stereotypesdb.qa.dto.ResultSummaryResponse;
import vaatz.stereotypesdb.qa.model.ResultItem;

/**
* @ClassName  : ResultService.java
* @Description: 결과 조회(목록/상세) 및 QA 전달 준비 로직
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
*/
@Service
public class ResultService {

    private static final int DEFAULT_DUMMY_COUNT = 42;
    private static final int NAVIGATION_WINDOW = 5;

    private final List<ResultItem> dummyResults;

    public ResultService() {
        this.dummyResults = Collections.unmodifiableList(generateDummyResults());
    }

    public ResultPageResponse getResultPage(int page, int size) {
        int safeSize = normalizeSize(size);
        int safePage = Math.max(0, page);
        int totalElements = dummyResults.size();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / safeSize);

        if (totalPages == 0) {
            ResultPageResponse empty = new ResultPageResponse();
            empty.setContents(Collections.emptyList());
            empty.setPage(0);
            empty.setSize(safeSize);
            empty.setTotalElements(0);
            empty.setTotalPages(0);
            empty.setHasPrevious(false);
            empty.setHasNext(false);
            empty.setNavigationPages(Collections.emptyList());
            return empty;
        }

        int clampedPage = Math.min(safePage, totalPages - 1);
        int fromIndex = clampedPage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<ResultSummaryResponse> contents = dummyResults.subList(fromIndex, toIndex)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        ResultPageResponse response = new ResultPageResponse();
        response.setContents(contents);
        response.setPage(clampedPage);
        response.setSize(safeSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setHasPrevious(clampedPage > 0);
        response.setHasNext(clampedPage < totalPages - 1);
        response.setNavigationPages(buildNavigation(clampedPage, totalPages));
        return response;
    }

    public ResultDetailResponse getResultDetail(Long resultId) {
        ResultItem item = findResult(resultId);
        ResultDetailResponse response = new ResultDetailResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setFileName(item.getFileName());
        response.setStatus(item.getStatus());
        response.setCreatedAt(item.getCreatedAt());
        // 현재는 DB/파일 미구현으로 비어 있는 값 유지
        response.setDocumentImageBase64(null);
        response.setHtmlTableContent(null);
        response.setExtractionSummary(null);
        return response;
    }

    public ResultItem findResult(Long resultId) {
        if (resultId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "resultId는 필수입니다.");
        }
        return dummyResults.stream()
                .filter(item -> item.getId().equals(resultId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "결과를 찾을 수 없습니다."));
    }

    public String buildQaPlaceholderHtml(ResultItem item) {
        return "<html><head><meta charset=\"UTF-8\" /><title>" + item.getTitle() +
                "</title></head><body><h1>" + item.getTitle() +
                "</h1><p>DB 연동 전 임시 HTML 입니다.</p></body></html>";
    }

    private ResultSummaryResponse toSummary(ResultItem item) {
        return new ResultSummaryResponse(item.getId(), item.getTitle(), item.getFileName(), item.getStatus(), item.getCreatedAt());
    }

    private List<ResultItem> generateDummyResults() {
        LocalDateTime base = LocalDateTime.now().minusDays(3);
        return IntStream.range(0, DEFAULT_DUMMY_COUNT)
                .mapToObj(i -> new ResultItem(
                        (long) i + 1,
                        String.format(Locale.KOREA, "추출결과 %02d", i + 1),
                        String.format(Locale.KOREA, "result_%02d.html", i + 1),
                        (i % 3 == 0) ? "대기" : (i % 3 == 1 ? "완료" : "오류"),
                        base.plusHours(i * 3L)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Integer> buildNavigation(int currentPage, int totalPages) {
        if (totalPages == 0) {
            return Collections.emptyList();
        }
        int start = Math.max(0, currentPage - NAVIGATION_WINDOW / 2);
        int end = Math.min(totalPages, start + NAVIGATION_WINDOW);
        if (end - start < NAVIGATION_WINDOW) {
            start = Math.max(0, end - NAVIGATION_WINDOW);
        }
        List<Integer> pages = new ArrayList<>();
        for (int page = start; page < end; page++) {
            pages.add(page + 1); // 프론트 표기는 1부터 시작
        }
        return pages;
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size는 1 이상이어야 합니다.");
        }
        if (size > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size는 50 이하로 제한됩니다.");
        }
        return size;
    }
}
