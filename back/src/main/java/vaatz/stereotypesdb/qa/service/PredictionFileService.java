package vaatz.stereotypesdb.qa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import vaatz.stereotypesdb.qa.dto.HtmlFileSaveRequest;
import vaatz.stereotypesdb.qa.dto.JsonMergeRequest;
import vaatz.stereotypesdb.qa.dto.JsonMergeResult;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileContentResponse;
import vaatz.stereotypesdb.qa.dto.WorkspaceFileResponse;
import vaatz.stereotypesdb.qa.model.WorkspaceFolderType;

/**
* @ClassName	: PredictionFileService.java
* @Description	: predict(s) JSON을 HTML로 분할하고 재병합하는 서비스.
* @Author		: GPT-5.1 Codex
* @Date			: 2025.11.28
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.28        GPT-5.1 Codex     - JSON 분할/병합 로직 최초 구현
*/
@Service
public class PredictionFileService {

    private static final Pattern DATA_TITLE_PATTERN = Pattern.compile("(?is)data-predict-title\\s*=\\s*['\"]([^'\"]+)['\"]");
    private static final Pattern TITLE_TAG_PATTERN = Pattern.compile("(?is)<title[^>]*>(.*?)</title>");
    private static final Pattern HEADING_PATTERN = Pattern.compile("(?is)<h[1-3][^>]*>(.*?)</h[1-3]>");
    private static final Pattern BODY_SECTION_PATTERN = Pattern.compile("(?is)<(div|section)[^>]*data-predict-body\\s*=\\s*['\"]true['\"][^>]*>(.*?)</\\1>");
    private static final Pattern BODY_TAG_PATTERN = Pattern.compile("(?is)<body[^>]*>(.*?)</body>");
    private static final Pattern TABLE_PATTERN = Pattern.compile("(?is)<table[^>]*>.*?</table>");
    private static final Pattern TAG_STRIP_PATTERN = Pattern.compile("(?is)<[^>]+>");
    private static final String[] TITLE_KEYS = {"title", "heading", "name", "id"};
    private static final String[] BODY_KEYS = {"predict", "html", "body", "content"};

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileWorkspaceService fileWorkspaceService;

    public PredictionFileService(FileWorkspaceService fileWorkspaceService) {
        this.fileWorkspaceService = fileWorkspaceService;
    }

    /**
     * 업로드된 JSON의 predict(s) 배열을 before HTML로 분리한다.
     */
    public List<WorkspaceFileResponse> splitJsonPredicts(MultipartFile jsonFile) {
        if (jsonFile == null || jsonFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 JSON 파일이 필요합니다.");
        }
        byte[] payload = readBytes(jsonFile);
        JsonNode root = parseJson(payload);
        ArrayNode predictArray = findPredictArray(root);
        if (predictArray == null || predictArray.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "predict(s) 배열을 찾을 수 없습니다.");
        }

        List<WorkspaceFileResponse> responses = new ArrayList<>();
        Set<String> usedFileNames = new HashSet<>();
        for (int i = 0; i < predictArray.size(); i++) {
            JsonNode entry = predictArray.get(i);
            String title = resolveTitle(entry, i);
            String body = resolvePredictBody(entry, i);
            String fileName = buildFileName(title, i, usedFileNames);

            HtmlFileSaveRequest saveRequest = new HtmlFileSaveRequest();
            saveRequest.setHtmlContent(buildHtmlDocument(title, body));
            WorkspaceFileResponse saved = fileWorkspaceService.saveHtmlContent(fileName, saveRequest);
            responses.add(saved);
        }
        return responses;
    }

    /**
     * 선택된 HTML을 predict 배열로 병합하고 after 폴더에 JSON을 저장한다.
     */
    public JsonMergeResult mergeHtmlFilesToJson(JsonMergeRequest request) {
        if (request == null || request.getFileNames() == null || request.getFileNames().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "병합할 HTML 파일명이 필요합니다.");
        }

        ArrayNode predictArray = objectMapper.createArrayNode();
        for (String fileName : request.getFileNames()) {
            WorkspaceFileContentResponse fileContent = fileWorkspaceService.readFile(WorkspaceFolderType.BEFORE, fileName);
            String html = fileContent.getContent();
            String title = extractTitleFromHtml(html, fileName);
            String body = extractBodyFromHtml(html);

            ObjectNode node = objectMapper.createObjectNode();
            node.put("title", title);
            node.put("predict", body);
            predictArray.add(node);
        }

        ObjectNode root = objectMapper.createObjectNode();
        root.set("predicts", predictArray);
        byte[] jsonBytes = writePrettyJson(root);

        String outputName = resolveOutputFileName(request.getOutputFileName());
        WorkspaceFileResponse saved = fileWorkspaceService.saveJsonToAfter(outputName, jsonBytes);

        JsonMergeResult result = new JsonMergeResult();
        result.setFileName(saved.getFileName());
        result.setJsonBytes(jsonBytes);
        result.setSavedFile(saved);
        return result;
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파일을 읽을 수 없습니다.", e);
        }
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

    private ArrayNode findPredictArray(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (node.isArray()) {
            if (looksLikePredictArray(node)) {
                return (ArrayNode) node;
            }
            for (JsonNode child : node) {
                ArrayNode nested = findPredictArray(child);
                if (nested != null) {
                    return nested;
                }
            }
            return null;
        }
        if (node.isObject()) {
            for (String key : new String[]{"predicts", "predict"}) {
                JsonNode candidate = node.get(key);
                if (candidate != null && candidate.isArray()) {
                    return (ArrayNode) candidate;
                }
            }
            Iterator<JsonNode> iterator = node.elements();
            while (iterator.hasNext()) {
                ArrayNode nested = findPredictArray(iterator.next());
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private boolean looksLikePredictArray(JsonNode node) {
        if (!node.isArray() || node.size() == 0) {
            return false;
        }
        for (JsonNode element : node) {
            if (element.isObject() && (element.has("predict") || element.has("title"))) {
                return true;
            }
        }
        return node.get(0).isTextual();
    }

    private String resolveTitle(JsonNode entry, int index) {
        if (entry != null && entry.isObject()) {
            for (String key : TITLE_KEYS) {
                JsonNode value = entry.get(key);
                if (value != null && value.isValueNode() && StringUtils.hasText(value.asText())) {
                    return value.asText().trim();
                }
            }
        }
        return String.format("predict-%02d", index + 1);
    }

    private String resolvePredictBody(JsonNode entry, int index) {
        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "predict 배열의 항목이 비어 있습니다. index=" + index);
        }
        if (entry.isObject()) {
            for (String key : BODY_KEYS) {
                JsonNode bodyNode = entry.get(key);
                if (bodyNode != null && bodyNode.isTextual()) {
                    return bodyNode.asText();
                }
            }
        }
        if (entry.isTextual()) {
            return entry.asText();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "predict 배열 항목에서 HTML 본문을 찾을 수 없습니다. index=" + index);
    }

    private String buildFileName(String title, int index, Set<String> used) {
        String base = slugify(title);
        if (!StringUtils.hasText(base)) {
            base = String.format("predict-%02d", index + 1);
        }
        String candidate = String.format("%03d_%s.html", index + 1, base);
        int suffix = 1;
        while (used.contains(candidate)) {
            candidate = String.format("%03d_%s_%d.html", index + 1, base, ++suffix);
        }
        used.add(candidate);
        return candidate;
    }

    private String slugify(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFKC);
        String replaced = normalized.replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        if (replaced.isEmpty()) {
            return "";
        }
        return replaced.length() > 80 ? replaced.substring(0, 80) : replaced;
    }

    private String buildHtmlDocument(String title, String body) {
        String safeTitle = title == null ? "" : title.trim();
        String attributeTitle = escapeHtmlAttribute(safeTitle);
        String textTitle = escapeHtmlText(safeTitle);
        String safeBody = body == null ? "" : body.trim();

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html lang='ko'>");
        builder.append("<head><meta charset='UTF-8'/><title>")
                .append(textTitle)
                .append("</title></head>");
        builder.append("<body data-predict-title='")
                .append(attributeTitle)
                .append("'>");
        builder.append("<h1>").append(textTitle).append("</h1>");
        builder.append("<div data-predict-body='true'>").append(safeBody).append("</div>");
        builder.append("</body></html>");
        return builder.toString();
    }

    private String escapeHtmlAttribute(String input) {
        String value = input == null ? "" : input;
        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String escapeHtmlText(String input) {
        String value = input == null ? "" : input;
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String extractTitleFromHtml(String html, String fallbackFileName) {
        if (StringUtils.hasText(html)) {
            Matcher matcher = DATA_TITLE_PATTERN.matcher(html);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
            matcher = TITLE_TAG_PATTERN.matcher(html);
            if (matcher.find()) {
                return stripTags(matcher.group(1));
            }
            matcher = HEADING_PATTERN.matcher(html);
            if (matcher.find()) {
                return stripTags(matcher.group(1));
            }
        }
        return removeExtension(fallbackFileName);
    }

    private String extractBodyFromHtml(String html) {
        if (!StringUtils.hasText(html)) {
            return "";
        }
        Matcher matcher = BODY_SECTION_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        matcher = TABLE_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(0).trim();
        }
        matcher = BODY_TAG_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return html.trim();
    }

    private String stripTags(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }
        return TAG_STRIP_PATTERN.matcher(input).replaceAll("").trim();
    }

    private String removeExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "predict";
        }
        String trimmed = fileName.trim();
        int idx = trimmed.lastIndexOf('.');
        if (idx > 0) {
            return trimmed.substring(0, idx);
        }
        return trimmed;
    }

    private String resolveOutputFileName(String candidate) {
        String base = StringUtils.hasText(candidate) ? candidate.trim() : "merged-predicts.json";
        if (!base.toLowerCase(Locale.ROOT).endsWith(".json")) {
            base = base + ".json";
        }
        return base;
    }
}

