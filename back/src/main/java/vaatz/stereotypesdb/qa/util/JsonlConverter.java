package vaatz.stereotypesdb.qa.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @ClassName	: JsonlConverter.java
* @Description	: HTML과 이미지를 JSONL 형식으로 변환하는 유틸리티 클래스
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- 시트 데이터를 JSONL 형식으로 변환
* 								- 각 시트마다 한 줄씩 JSONL 형식으로 출력
*/
public class JsonlConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTML과 이미지를 JSONL 형식으로 변환
     * @param htmlContent HTML 내용 (base64 인코딩하지 않음)
     * @param imageBase64 Base64로 인코딩된 이미지 (null 가능)
     * @return JSONL 한 줄
     */
    public static String toJsonlLine(String htmlContent, String imageBase64) {
        try {
            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("image", imageBase64 != null ? imageBase64 : "");
            // HTML은 그대로 저장 (base64 인코딩하지 않음)
            payload.put("html", htmlContent != null ? htmlContent : "");
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSONL 변환 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 여러 시트 데이터를 JSONL 형식으로 변환 (각 시트마다 한 줄씩)
     * @param sheets 시트 데이터 리스트
     * @return JSONL 형식의 문자열 (각 줄은 한 시트를 나타냄)
     */
    public static String toJsonl(List<ExcelToHtmlConverter.SheetData> sheets) {
        return sheets.stream()
                .map(sheet -> toJsonlLine(sheet.getHtmlContent(), sheet.getImageBase64()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 여러 시트 데이터를 JSONL 형식의 바이트 배열로 변환
     * @param sheets 시트 데이터 리스트
     * @return JSONL 형식의 바이트 배열
     */
    public static byte[] toJsonlBytes(List<ExcelToHtmlConverter.SheetData> sheets) {
        return toJsonl(sheets).getBytes(StandardCharsets.UTF_8);
    }
}

