package vaatz.stereotypesdb.qa.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        Workbook workbook;
        
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. xlsx 또는 xls 파일만 지원합니다.");
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

    private static String convertSheetToHtml(Sheet sheet) {
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>\n");

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        if (firstRowNum > lastRowNum) {
            html.append("</table>");
            return html.toString();
        }

        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            html.append("<tr>\n");

            if (row == null) {
                html.append("<td></td>\n");
                html.append("</tr>\n");
                continue;
            }

            int firstCellNum = row.getFirstCellNum();
            int lastCellNum = row.getLastCellNum();

            for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                
                if (isCellInMergedRegion(cellNum, rowNum, mergedRegions)) {
                    CellRangeAddress mergedRegion = getMergedRegion(cellNum, rowNum, mergedRegions);
                    if (mergedRegion.getFirstRow() == rowNum && mergedRegion.getFirstColumn() == cellNum) {
                        int rowspan = mergedRegion.getLastRow() - mergedRegion.getFirstRow() + 1;
                        int colspan = mergedRegion.getLastColumn() - mergedRegion.getFirstColumn() + 1;
                        String cellValue = getCellValueAsString(cell);
                        html.append(String.format("<td rowspan='%d' colspan='%d'>%s</td>\n", 
                            rowspan, colspan, escapeHtml(cellValue)));
                    }
                } else {
                    String cellValue = getCellValueAsString(cell);
                    html.append("<td>").append(escapeHtml(cellValue)).append("</td>\n");
                }
            }

            html.append("</tr>\n");
        }

        html.append("</table>");
        return html.toString();
    }

    private static boolean isCellInMergedRegion(int col, int row, List<CellRangeAddress> mergedRegions) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row, col)) {
                return true;
            }
        }
        return false;
    }

    private static CellRangeAddress getMergedRegion(int col, int row, List<CellRangeAddress> mergedRegions) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row, col)) {
                return region;
            }
        }
        return null;
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

