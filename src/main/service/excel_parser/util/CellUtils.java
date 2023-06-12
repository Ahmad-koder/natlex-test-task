package main.service.excel_parser.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;

public class CellUtils {
    private CellUtils() {
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ?
                    cell.getDateCellValue() :
                    (cell.getNumericCellValue() % 1) == 0 ?
                            (int) cell.getNumericCellValue() :
                            BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> getFormulaValue(cell);
            case ERROR -> String.valueOf(cell.getErrorCellValue());
            default ->
                    throw new RuntimeException("не определен тип ячейки: " + cell.getAddress().formatAsString());
        };
    }

    private static Object getFormulaValue(Cell cell) {
        return switch (cell.getCachedFormulaResultType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> throw new RuntimeException("не определен тип результат формулы для ячейки: " + cell.getAddress().formatAsString());
        };
    }
}
