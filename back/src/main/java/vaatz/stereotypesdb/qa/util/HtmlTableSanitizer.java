package vaatz.stereotypesdb.qa.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @ClassName	: HtmlTableSanitizer.java
* @Description	: HTML 테이블에 기본 보더 속성을 주입하고 불필요한 개행을 제거하는 유틸
* @Author		: 정은주
* @Date			: 2025.11.27
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.27        정은주       	- 테이블 border/cellspacing/cellpadding 강제
* 								- style에 border-collapse 추가 및 개행 제거
*/
public final class HtmlTableSanitizer {

    private static final Pattern TABLE_PATTERN = Pattern.compile("(?i)<table([^>]*)>");
    private static final Pattern STYLE_DOUBLE_PATTERN = Pattern.compile("(?i)style\\s*=\\s*\"([^\"]*)\"");
    private static final Pattern STYLE_SINGLE_PATTERN = Pattern.compile("(?i)style\\s*=\\s*'([^']*)'");
    private static final Pattern ESCAPED_QUOTE_PATTERN = Pattern.compile("\\\\([\"'])");

    private HtmlTableSanitizer() {
    }

    /**
     * 테이블 태그 속성을 보완하고 \r, \n 개행을 제거한다.
     */
    public static String normalize(String html) {
        if (html == null) {
            return "";
        }
        String trimmed = html.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        String withAttributes = ensureTableAttributes(trimmed);
        return removeLineBreaks(withAttributes);
    }

    private static String ensureTableAttributes(String html) {
        Matcher matcher = TABLE_PATTERN.matcher(html);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String attrs = matcher.group(1);
            String updatedAttrs = enrichAttributes(attrs);
            matcher.appendReplacement(buffer,
                    Matcher.quoteReplacement("<table" + updatedAttrs + ">"));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String enrichAttributes(String attrs) {
        String attrPortion = normalizeEscapedQuotes(attrs);
        attrPortion = ensureStyle(attrPortion);
        String lower = attrPortion.toLowerCase(Locale.ROOT);

        StringBuilder builder = new StringBuilder(attrPortion);
        if (!lower.contains("border=")) {
            builder.append(" border='1'");
        }
        if (!lower.contains("cellspacing=")) {
            builder.append(" cellspacing='0'");
        }
        if (!lower.contains("cellpadding=")) {
            builder.append(" cellpadding='6'");
        }
        return builder.toString();
    }

    private static String ensureStyle(String attrs) {
        String current = attrs == null ? "" : attrs;

        Matcher doubleMatcher = STYLE_DOUBLE_PATTERN.matcher(current);
        if (doubleMatcher.find()) {
            String value = doubleMatcher.group(1);
            String updated = appendStyleRules(value);
            if (!value.equals(updated)) {
                return replace(current, doubleMatcher.start(1), doubleMatcher.end(1), updated);
            }
            return current;
        }

        Matcher singleMatcher = STYLE_SINGLE_PATTERN.matcher(current);
        if (singleMatcher.find()) {
            String value = singleMatcher.group(1);
            String updated = appendStyleRules(value);
            if (!value.equals(updated)) {
                return replace(current, singleMatcher.start(1), singleMatcher.end(1), updated);
            }
            return current;
        }

        String styleAttr = " style='border-collapse:collapse'";
        if (current.trim().isEmpty()) {
            return styleAttr;
        }
        return current + styleAttr;
    }

    /**
     * 백슬래시로 이스케이프된 따옴표를 실제 따옴표로 되돌린다.
     */
    private static String normalizeEscapedQuotes(String attrs) {
        if (attrs == null) {
            return "";
        }
        if (attrs.isEmpty() || attrs.indexOf('\\') < 0) {
            return attrs;
        }
        return ESCAPED_QUOTE_PATTERN.matcher(attrs).replaceAll("$1");
    }

    private static String appendStyleRules(String styleValue) {
        String trimmed = styleValue.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder(trimmed);

        if (builder.length() > 0 && builder.charAt(builder.length() - 1) != ';') {
            builder.append(';');
        }
        if (!lower.contains("border-collapse")) {
            builder.append("border-collapse:collapse;");
        }
        return builder.toString();
    }

    private static String replace(String original, int start, int end, String replacement) {
        return new StringBuilder(original)
                .replace(start, end, replacement)
                .toString();
    }

    private static String removeLineBreaks(String html) {
        return html.replace("\r", "").replace("\n", "");
    }
}
