package vaatz.stereotypesdb.qa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import vaatz.stereotypesdb.qa.dto.JsonPredictConversionResponse;
import vaatz.stereotypesdb.qa.dto.JsonPredictConversionResponse.RecordSummary;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.model.HtmlSheetData;
import vaatz.stereotypesdb.qa.service.FileWorkspaceService;
import vaatz.stereotypesdb.qa.util.HtmlTableSanitizer;
import vaatz.stereotypesdb.qa.util.JsonlConverter;

/**
 * JSON 파일의 predict 필드를 HTML 테이블로 변환하고 JSON/JSONL 파일을 생성하는 서비스.
 */
@Service
public class JsonPredictConversionService {

    private static final String TABLE_OPEN = "<table border='1' cellspacing='0' cellpadding='6' style='border-collapse:collapse;'>";
    private static final String TABLE_CLOSE = "</table>";
    private static final List<String> IMAGE_KEYS = Arrays.asList("imagePath", "image_path", "image", "img_path", "imgPath");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileWorkspaceService fileWorkspaceService;

    public JsonPredictConversionService(FileWorkspaceService fileWorkspaceService) {
        this.fileWorkspaceService = fileWorkspaceService;
    }

    public JsonPredictConversionResponse convert(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 JSON 파일이 필요합니다.");
        }
        byte[] sourceBytes = readBytes(file);
        JsonNode root = parseJson(sourceBytes);

        List<RecordSummary> summaries = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(1);
        processNode(root, summaries, counter);

        byte[] updatedJsonBytes = writePrettyJson(root);
        List<HtmlSheetData> sheetDataList = new ArrayList<>();
        for (RecordSummary summary : summaries) {
            sheetDataList.add(new HtmlSheetData(
                    resolveSheetName(summary.getIdentifier(), summary.getImagePath(), sheetDataList.size()),
                    summary.getPredictHtml(),
                    summary.getImagePath()));
        }
        byte[] jsonlBytes = JsonlConverter.toJsonlBytes(sheetDataList);

        WorkspaceFileResponse jsonFileResponse = fileWorkspaceService.saveJsonContent(file.getOriginalFilename(), updatedJsonBytes);
        String jsonlFileName = toJsonlFileName(jsonFileResponse.getFileName());
        fileWorkspaceService.saveJsonlToAfter(jsonlFileName, jsonlBytes);

        JsonPredictConversionResponse response = new JsonPredictConversionResponse();
        response.setJsonFileName(jsonFileResponse.getFileName());
        response.setJsonFileBase64(updatedJsonBytes);
        response.setJsonlFileName(jsonlFileName);
        response.setJsonlFileBase64(jsonlBytes);
        response.setRecords(summaries);
        return response;
    }

    private void processNode(JsonNode node, List<RecordSummary> summaries, AtomicInteger counter) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            JsonNode predictNode = objectNode.get("predict");
            if (predictNode != null && predictNode.isTextual()) {
                String imagePath = resolveImagePath(objectNode);
                String sanitizedHtml = sanitizePredict(predictNode.asText(), imagePath);
                objectNode.put("predict", sanitizedHtml);
                String identifier = resolveIdentifier(objectNode, imagePath, counter);
                summaries.add(new RecordSummary(identifier, imagePath, sanitizedHtml));
            }
            objectNode.fields().forEachRemaining(entry -> processNode(entry.getValue(), summaries, counter));
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.forEach(child -> processNode(child, summaries, counter));
        }
    }

    private String sanitizePredict(String raw, String imagePath) {
        String trimmed = raw == null ? "" : raw.trim();
        String html;
        if (trimmed.toLowerCase(Locale.ROOT).contains("<table")) {
            html = HtmlTableSanitizer.normalize(trimmed);
        } else {
            html = buildTableFromPlain(trimmed);
        }
        return appendImage(html, imagePath);
    }

    private String buildTableFromPlain(String text) {
        if (!StringUtils.hasText(text)) {
            return TABLE_OPEN + TABLE_CLOSE;
        }
        String[] lines = text.split("\\r?\\n");
        StringBuilder html = new StringBuilder(TABLE_OPEN).append("<tbody>");
        int rowCount = 0;
        for (String line : lines) {
            if (!StringUtils.hasText(line)) {
                continue;
            }
            String[] cells = splitLine(line);
            html.append("<tr>");
            for (String cell : cells) {
                html.append("<td>").append(escapeHtml(cell.trim())).append("</td>");
            }
            html.append("</tr>");
            rowCount++;
        }
        if (rowCount == 0) {
            html.append("<tr><td></td></tr>");
        }
        html.append("</tbody>").append(TABLE_CLOSE);
        return html.toString();
    }

    private String[] splitLine(String line) {
        if (line.contains("\t")) {
            return line.split("\\t");
        }
        if (line.contains("|")) {
            return line.split("\\|");
        }
        if (line.contains(",")) {
            return line.split("\\s*,\\s*");
        }
        if (line.contains("  ")) {
            return line.trim().split("\\s{2,}");
        }
        return new String[]{line};
    }

    private String appendImage(String html, String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return html;
        }
        return html + "<div style='margin-top:12px;text-align:left;'>"
                + "<img src='" + escapeAttribute(imagePath) + "' alt='predict image' style='max-width:100%;height:auto;'/>"
                + "</div>";
    }

    private String resolveImagePath(ObjectNode objectNode) {
        for (String key : IMAGE_KEYS) {
            JsonNode candidate = objectNode.get(key);
            if (candidate != null && candidate.isTextual() && StringUtils.hasText(candidate.asText())) {
                return candidate.asText();
            }
        }
        return "";
    }

    private String resolveIdentifier(ObjectNode objectNode, String imagePath, AtomicInteger counter) {
        String titleFromImage = deriveTitleFromImagePath(imagePath);
        if (StringUtils.hasText(titleFromImage)) {
            return titleFromImage;
        }
        JsonNode idNode = objectNode.get("id");
        if (idNode != null && idNode.isValueNode() && StringUtils.hasText(idNode.asText())) {
            return idNode.asText();
        }
        return "row-" + counter.getAndIncrement();
    }

    private String resolveSheetName(String identifier, String imagePath, int index) {
        if (StringUtils.hasText(identifier)) {
            return identifier;
        }
        String titleFromImage = deriveTitleFromImagePath(imagePath);
        if (StringUtils.hasText(titleFromImage)) {
            return titleFromImage;
        }
        return "sheet-" + (index + 1);
    }

    private String escapeHtml(String text) {
        String result = text;
        result = result.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        result = result.replace("'", "&#39;");
        return result;
    }

    private String escapeAttribute(String text) {
        String trimmed = text == null ? "" : text.trim();
        return escapeHtml(trimmed);
    }

    private String deriveTitleFromImagePath(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return "";
        }
        String normalized = imagePath.trim();
        int queryIdx = normalized.indexOf('?');
        if (queryIdx >= 0) {
            normalized = normalized.substring(0, queryIdx);
        }
        normalized = normalized.replace('\\', '/');
        int slashIdx = normalized.lastIndexOf('/');
        String fileName = slashIdx >= 0 ? normalized.substring(slashIdx + 1) : normalized;
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) {
            return fileName.substring(0, dotIdx);
        }
        return fileName;
    }

    private String toJsonlFileName(String jsonFileName) {
        String base = jsonFileName == null ? "output.json" : jsonFileName.trim();
        int idx = base.lastIndexOf('.');
        if (idx > 0) {
            return base.substring(0, idx) + ".jsonl";
        }
        return base + ".jsonl";
    }

    private JsonNode parseJson(byte[] payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON 파싱에 실패했습니다.", e);
        }
    }

    private byte[] writePrettyJson(JsonNode node) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(node);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 직렬화에 실패했습니다.", e);
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 읽을 수 없습니다.", e);
        }
    }
}
