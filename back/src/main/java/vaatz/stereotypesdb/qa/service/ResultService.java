package vaatz.stereotypesdb.qa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Pipeline;
import vaatz.stereotypesdb.qa.domain.Result;
import vaatz.stereotypesdb.qa.domain.ResultSheet;
import vaatz.stereotypesdb.qa.dto.ResultDetailResponse;
import vaatz.stereotypesdb.qa.dto.ResultRequest;
import vaatz.stereotypesdb.qa.dto.ResultSheetRequest;
import vaatz.stereotypesdb.qa.dto.ResultSheetResponse;
import vaatz.stereotypesdb.qa.dto.ResultSummaryResponse;
import vaatz.stereotypesdb.qa.repository.PipelineRepository;
import vaatz.stereotypesdb.qa.repository.ResultRepository;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private final ResultRepository resultRepository;
    private final PipelineRepository pipelineRepository;
    private final ObjectMapper objectMapper;

    public ResultService(ResultRepository resultRepository,
                         PipelineRepository pipelineRepository,
                         ObjectMapper objectMapper) {
        this.resultRepository = resultRepository;
        this.pipelineRepository = pipelineRepository;
        this.objectMapper = objectMapper;
    }

    public ResultSummaryResponse getSummary() {
        ResultSummaryResponse response = new ResultSummaryResponse();
        long total = resultRepository.count();
        Long completed = resultRepository.countByStatus("COMPLETED");
        Long failed = resultRepository.countByStatus("FAILED");
        response.setTotalDocuments(total);
        response.setCompletedDocuments(completed);
        response.setFailedDocuments(failed);
        return response;
    }

    public List<ResultDetailResponse> getAll(Long pipelineId) {
        List<Result> results;
        if (pipelineId != null) {
            results = resultRepository.findByPipelineId(pipelineId);
        } else {
            results = resultRepository.findAll();
        }
        return results.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public ResultDetailResponse getById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        return toDetailResponse(result);
    }

    public ResultDetailResponse create(ResultRequest request) {
        Pipeline pipeline = pipelineRepository.findById(request.getPipelineId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 파이프라인을 찾을 수 없습니다."));
        Result result = new Result();
        applyRequest(result, request);
        result.setPipeline(pipeline);
        return toDetailResponse(resultRepository.save(result));
    }

    public ResultDetailResponse update(Long id, ResultRequest request) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        Pipeline pipeline = pipelineRepository.findById(request.getPipelineId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "연결할 파이프라인을 찾을 수 없습니다."));
        applyRequest(result, request);
        result.setPipeline(pipeline);
        return toDetailResponse(resultRepository.save(result));
    }

    public void delete(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        resultRepository.delete(result);
    }

    private void applyRequest(Result result, ResultRequest request) {
        result.setDocumentName(request.getDocumentName());
        result.setStatus(request.getStatus());
        result.setActualFileSize(request.getActualFileSize());
        result.setTransferredFileSize(request.getTransferredFileSize());
        result.setStartedAt(request.getStartedAt());
        result.setFinishedAt(request.getFinishedAt());
        result.setOriginalFileName(request.getOriginalFileName());
        result.setOriginalFileSize(request.getOriginalFileSize());
        result.setConvertedFileSize(request.getConvertedFileSize());
        result.setOriginalViewerUri(request.getOriginalViewerUri());
        result.setHtmlRenderUri(request.getHtmlRenderUri());
        result.setMetadata(request.getMetadata());
        syncSheets(result, request.getSheets());
    }

    private ResultDetailResponse toDetailResponse(Result result) {
        ResultDetailResponse response = new ResultDetailResponse();
        response.setId(result.getId());
        response.setDocumentName(result.getDocumentName());
        response.setStatus(result.getStatus());
        response.setActualFileSize(result.getActualFileSize());
        response.setTransferredFileSize(result.getTransferredFileSize());
        response.setStartedAt(result.getStartedAt());
        response.setFinishedAt(result.getFinishedAt());
        response.setOriginalFileName(result.getOriginalFileName());
        response.setOriginalFileSize(result.getOriginalFileSize());
        response.setConvertedFileSize(result.getConvertedFileSize());
        response.setOriginalViewerUri(result.getOriginalViewerUri());
        response.setHtmlRenderUri(result.getHtmlRenderUri());
        response.setMetadata(result.getMetadata());
        response.setSheets(result.getSheets().stream()
                .map(this::toSheetResponse)
                .collect(Collectors.toList()));
        return response;
    }

    private ResultSheetResponse toSheetResponse(ResultSheet sheet) {
        ResultSheetResponse response = new ResultSheetResponse();
        response.setId(sheet.getId());
        response.setSheetName(sheet.getSheetName());
        response.setSheetOrder(sheet.getSheetOrder());
        response.setHtmlContent(sheet.getHtmlContent());
        response.setImageBase64(sheet.getImageBase64());
        return response;
    }

    private void syncSheets(Result result, List<ResultSheetRequest> sheetRequests) {
        if (sheetRequests == null) {
            return;
        }
        result.getSheets().clear();
        int nextOrder = 0;
        for (ResultSheetRequest request : sheetRequests) {
            if (request == null) {
                continue;
            }
            ResultSheet sheet = new ResultSheet();
            sheet.setSheetName(request.getSheetName());
            Integer order = request.getSheetOrder();
            if (order == null) {
                order = nextOrder;
            }
            sheet.setSheetOrder(order);
            if (order >= nextOrder) {
                nextOrder = order + 1;
            }
            sheet.setHtmlContent(request.getHtmlContent());
            sheet.setImageBase64(request.getImageBase64());
            sheet.setResult(result);
            result.getSheets().add(sheet);
        }
        result.getSheets().sort(Comparator.comparingInt(sheet -> sheet.getSheetOrder() != null ? sheet.getSheetOrder() : 0));
    }

    public byte[] buildJsonl(Long resultId) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 결과를 찾을 수 없습니다."));
        String jsonl = result.getSheets().stream()
                .sorted(Comparator.comparingInt(sheet -> sheet.getSheetOrder() != null ? sheet.getSheetOrder() : 0))
                .map(this::toJsonlLine)
                .collect(Collectors.joining("\n"));
        if (jsonl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "내보낼 시트 데이터가 없습니다.");
        }
        return jsonl.getBytes(StandardCharsets.UTF_8);
    }

    private String toJsonlLine(ResultSheet sheet) {
        try {
            String html = sheet.getHtmlContent() == null ? "" :
                    java.util.Base64.getEncoder().encodeToString(sheet.getHtmlContent().getBytes(StandardCharsets.UTF_8));
            String image = sheet.getImageBase64() == null ? "" : sheet.getImageBase64();
            java.util.Map<String, String> payload = new java.util.LinkedHashMap<>();
            payload.put("image", image);
            payload.put("html", html);
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSONL 생성 중 오류가 발생했습니다.", e);
        }
    }
}

