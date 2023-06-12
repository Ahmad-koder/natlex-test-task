package main.service.excel_parser.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class RowUtils {
    private RowUtils() {
    }

    public static Integer getFirstNonEmptyRowNum(Sheet sheet) {
        final var firstRowNum = sheet.getFirstRowNum();
        final var lastRowNum = sheet.getLastRowNum();

        if (firstRowNum == -1)
            return -1;

        for (int rowNum = firstRowNum; rowNum < lastRowNum; rowNum++) {
            final var row = sheet.getRow(rowNum);
            final var rowIsEmpty = checkIfRowIsEmpty(row);

            if (!rowIsEmpty)
                return rowNum;
        }
        return -1;
    }

    private static boolean checkIfRowIsEmpty(Row row) {
        if (row == null)
            return true;

        if (row.getLastCellNum() <= 0)
            return true;

        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            final var cell = row.getCell(cellNum);
            if (cell != null) {
                final var cellType = cell.getCellType();
                final var str = cell.toString();
                if (cellType != CellType.BLANK && StringUtils.hasText(str))
                    return false;
            }
        }
        return true;
    }
}
