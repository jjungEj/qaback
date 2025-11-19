package vaatz.stereotypesdb.qa.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * HTML 파일에서 테이블(<table>)을 추출해 SheetData 구조로 변환하는 유틸리티.
 */
public final class HtmlTableParser {

    private HtmlTableParser() {
    }

    public static List<ExcelToHtmlConverter.SheetData> parse(InputStream inputStream, String fileName) throws IOException {
        Document document = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");
        Elements tables = document.select("table");

        if (tables.isEmpty()) {
            throw new IllegalArgumentException("HTML 파일에서 <table> 요소를 찾을 수 없습니다.");
        }

        List<ExcelToHtmlConverter.SheetData> sheets = new ArrayList<>();
        String baseSheetName = stripExtension(fileName);

        int index = 1;
        for (Element table : tables) {
            String sheetName = resolveSheetName(table, baseSheetName, index);
            sheets.add(new ExcelToHtmlConverter.SheetData(sheetName, table.outerHtml(), null));
            index++;
        }

        return sheets;
    }

    private static String resolveSheetName(Element table, String baseSheetName, int index) {
        String declaredName = table.attr("data-sheet-name").trim();
        if (!declaredName.isEmpty()) {
            return declaredName;
        }
        return baseSheetName + "_table" + index;
    }

    private static String stripExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "sheet";
        }
        String trimmed = fileName.trim();
        int dotIndex = trimmed.lastIndexOf('.');
        if (dotIndex > 0) {
            return trimmed.substring(0, dotIndex);
        }
        return trimmed;
    }
}
