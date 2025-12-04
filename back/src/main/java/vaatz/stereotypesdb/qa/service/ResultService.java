package vaatz.stereotypesdb.qa.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultPageResponse;
import vaatz.stereotypesdb.qa.dto.ResultSummaryResponse;
import vaatz.stereotypesdb.qa.model.ResultEntity;
import vaatz.stereotypesdb.qa.repository.ResultRepository;

/**
* @ClassName  : ResultService.java
* @Description: 결과 조회(목록/상세) 및 QA 전달 준비 로직
* @Author     : GPT-5.1 Codex
* @Date       : 2025.12.04
*/
@Service
public class ResultService {

    private static final int NAVIGATION_WINDOW = 5;

    private final ResultRepository resultRepository;

    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public ResultPageResponse getResultPage(int page, int size) {
        int safeSize = normalizeSize(size);
        int safePage = Math.max(0, page);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ResultEntity> resultPage = resultRepository.findAll(pageable);

        ResultPageResponse response = new ResultPageResponse();
        response.setContents(resultPage.getContent().stream().map(this::toSummary).collect(Collectors.toList()));
        response.setPage(resultPage.getNumber());
        response.setSize(resultPage.getSize());
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setHasPrevious(resultPage.hasPrevious());
        response.setHasNext(resultPage.hasNext());
        response.setNavigationPages(buildNavigation(resultPage.getNumber(), resultPage.getTotalPages()));
        return response;
    }

    public ResultDetailResponse getResultDetail(Long resultId) {
        ResultEntity entity = findResult(resultId);
        ResultDetailResponse response = new ResultDetailResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setFileName(entity.getFileName());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setDocumentImageBase64(entity.getDocumentImageBase64());
        response.setHtmlTableContent(entity.getHtmlTableContent());
        response.setExtractionSummary(entity.getExtractionSummary());
        return response;
    }

    public ResultEntity findResult(Long resultId) {
        if (resultId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "resultId는 필수입니다.");
        }
        return resultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "결과를 찾을 수 없습니다."));
    }

    public String resolveQaHtmlContent(ResultEntity entity) {
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결과 정보를 찾을 수 없습니다.");
        }
        if (StringUtils.hasText(entity.getHtmlTableContent())) {
            return entity.getHtmlTableContent();
        }
        String title = StringUtils.hasText(entity.getTitle()) ? entity.getTitle() : "결과 상세";
        return "<html><head><meta charset=\"UTF-8\" /><title>" + title +
                "</title></head><body><h1>" + title +
                "</h1><p>DB 연동 전 임시 HTML 입니다.</p></body></html>";
    }

    private ResultSummaryResponse toSummary(ResultEntity entity) {
        return new ResultSummaryResponse(entity.getId(), entity.getTitle(), entity.getFileName(), entity.getStatus(), entity.getCreatedAt());
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
