package vaatz.stereotypesdb.qa.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
* @ClassName	: ExcelToHtmlConverter.java
* @Description	: 엑셀 파일(xlsx, xls, csv)을 HTML 테이블 형식으로 변환하는 유틸리티 클래스
* @Author		: 정은주
* @Date			: 2025.11.17
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.11.17        정은주       	- Apache POI를 사용한 엑셀 파일 파싱
* 								- 병합 셀 처리 및 HTML 이스케이프 처리
* 								- CSV 파일 파싱 지원
*/
public class ExcelToHtmlConverter {

    public static class SheetData {
        private String sheetName;
        private String htmlContent;
        private String imageBase64;

        public SheetData(String sheetName, String htmlContent, String imageBase64) {
            this.sheetName = sheetName;
            this.htmlContent = htmlContent;
            this.imageBase64 = imageBase64;
        }

        public String getSheetName() {
            return sheetName;
        }

        public String getHtmlContent() {
            return htmlContent;
        }

        public String getImageBase64() {
            return imageBase64;
        }
    }

    public static List<SheetData> convertToHtml(InputStream inputStream, String fileName) throws IOException {
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.endsWith(".csv")) {
            return convertCsvToHtml(inputStream, fileName);
        } else if (lowerFileName.endsWith(".xlsx")) {
            return convertExcelToHtml(inputStream, true);
        } else if (lowerFileName.endsWith(".xls")) {
            return convertExcelToHtml(inputStream, false);
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. xlsx, xls, csv 파일만 지원합니다.");
        }
    }

    private static List<SheetData> convertExcelToHtml(InputStream inputStream, boolean isXlsx) throws IOException {
        Workbook workbook;
        
        if (isXlsx) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            workbook = new HSSFWorkbook(inputStream);
        }

        List<SheetData> sheets = new ArrayList<>();
        
        try {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                String htmlContent = convertSheetToHtml(sheet);
                sheets.add(new SheetData(sheetName, htmlContent, null));
            }
        } finally {
            workbook.close();
        }

        return sheets;
    }

    private static List<SheetData> convertCsvToHtml(InputStream inputStream, String fileName) throws IOException {
        List<SheetData> sheets = new ArrayList<>();
        String sheetName = fileName.substring(0, fileName.lastIndexOf('.'));
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            StringBuilder html = new StringBuilder();
            html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>\n");
            
            String line;
            while ((line = reader.readLine()) != null) {
                html.append("<tr>\n");
                String[] cells = parseCsvLine(line);
                for (String cell : cells) {
                    html.append("<td>").append(escapeHtml(cell)).append("</td>\n");
                }
                html.append("</tr>\n");
            }
            
            html.append("</table>");
            sheets.add(new SheetData(sheetName, html.toString(), null));
        }
        
        return sheets;
    }

    private static String[] parseCsvLine(String line) {
        List<String> cells = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentCell = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 이스케이프된 따옴표
                    currentCell.append('"');
                    i++;
                } else {
                    // 따옴표 시작/끝
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 셀 구분자
                cells.add(currentCell.toString());
                currentCell = new StringBuilder();
            } else {
                currentCell.append(c);
            }
        }
        
        // 마지막 셀 추가
        cells.add(currentCell.toString());
        
        return cells.toArray(new String[0]);
    }

    private static String convertSheetToHtml(Sheet sheet) {
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>\n");

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        if (sheet.getPhysicalNumberOfRows() == 0 || firstRowNum > lastRowNum) {
            html.append("</table>");
            return html.toString();
        }

        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        int maxColumnNum = getMaxColumnNumber(sheet);

        for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            html.append("<tr>\n");

            for (int cellNum = 0; cellNum < maxColumnNum; cellNum++) {
                CellRangeAddress mergedRegion = getMergedRegion(cellNum, rowNum, mergedRegions);
                if (mergedRegion != null) {
                    if (mergedRegion.getFirstRow() == rowNum && mergedRegion.getFirstColumn() == cellNum) {
                        int rowspan = mergedRegion.getLastRow() - mergedRegion.getFirstRow() + 1;
                        int colspan = mergedRegion.getLastColumn() - mergedRegion.getFirstColumn() + 1;
                        Cell cell = row != null ? row.getCell(cellNum) : null;
                        String cellValue = getCellValueAsString(cell);
                        html.append(String.format("<td rowspan='%d' colspan='%d'>%s</td>\n",
                                rowspan, colspan, escapeHtml(cellValue)));
                    }
                    continue;
                }

                Cell cell = row != null ? row.getCell(cellNum) : null;
                String cellValue = getCellValueAsString(cell);
                html.append("<td>").append(escapeHtml(cellValue)).append("</td>\n");
            }

            html.append("</tr>\n");
        }

        html.append("</table>");
        return html.toString();
    }

    private static CellRangeAddress getMergedRegion(int col, int row, List<CellRangeAddress> mergedRegions) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row, col)) {
                return region;
            }
        }
        return null;
    }

    private static int getMaxColumnNumber(Sheet sheet) {
        int maxColumnNum = 0;
        for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            short lastCellNum = row.getLastCellNum();
            if (lastCellNum > maxColumnNum) {
                maxColumnNum = lastCellNum;
            }
        }
        return maxColumnNum;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}

