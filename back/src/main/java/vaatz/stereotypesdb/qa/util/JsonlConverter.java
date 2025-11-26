package vaatz.stereotypesdb.qa.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import vaatz.stereotypesdb.qa.model.HtmlSheetData;

/**
* @ClassName	: JsonlConverter.java
* @Description	: HTML과 이미지를 JSONL 형식으로 변환하는 유틸리티 클래스
* @Author		: 정은주
* @Date			: 2025.11.25
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.25        정은주       	- 시트 데이터를 JSONL 형식으로 변환
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
    public static String toJsonl(List<HtmlSheetData> sheets) {
        return sheets.stream()
                .map(sheet -> toJsonlLine(sheet.getHtmlContent(), sheet.getImageBase64()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 여러 시트 데이터를 JSONL 형식의 바이트 배열로 변환
     * @param sheets 시트 데이터 리스트
     * @return JSONL 형식의 바이트 배열
     */
    public static byte[] toJsonlBytes(List<HtmlSheetData> sheets) {
        return toJsonl(sheets).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * HTML 내용에서 테이블 태그를 추출한다.
     * 
     * 하나의 HTML에 여러 개의 <table> 태그가 포함된 경우, 각 테이블을 개별적으로 추출하여 리스트로 반환한다.
     * 예: "<table>테이블1</table><table>테이블2</table>" -> ["<table>테이블1</table>", "<table>테이블2</table>"]
     * 
     * @param htmlContent HTML 내용 (여러 테이블이 포함될 수 있음)
     * @return 테이블 태그 리스트 (각 테이블은 완전한 <table>...</table> 형태)
     *         테이블이 없거나 HTML이 비어있으면 빈 리스트 반환
     */
    public static List<String> extractTables(String htmlContent) {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tables = new ArrayList<>();
        // <table> 태그를 찾기 위한 정규식
        // - <table[^>]*>: <table> 태그 시작 (속성 포함)
        // - .*?: 태그 내용 (비탐욕적 매칭)
        // - </table>: 태그 종료
        // Pattern.DOTALL: .이 개행문자도 매칭하도록 설정
        // Pattern.CASE_INSENSITIVE: 대소문자 구분 안 함
        Pattern pattern = Pattern.compile("<table[^>]*>.*?</table>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlContent);

        // 모든 매칭되는 테이블을 리스트에 추가
        while (matcher.find()) {
            tables.add(matcher.group());
        }

        return tables;
    }

    /**
     * HTML 내용에서 테이블 개수를 센다.
     * 
     * extractTables() 메서드를 사용하여 테이블을 추출한 후 개수를 반환한다.
     * 테이블이 2개 이상인 경우 JSONL 변환 시 각 테이블을 별도 라인으로 분리하는데 사용된다.
     * 
     * @param htmlContent HTML 내용
     * @return 테이블 개수 (0개 이상)
     */
    public static int countTables(String htmlContent) {
        return extractTables(htmlContent).size();
    }
}

