package main.service.excel_parser;

import main.model.File;
import main.service.excel_parser.mapper.ExcelMapper;
import main.service.excel_parser.util.RowUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class XLSParserService<T> {
    public static final String MIME_APPLICATION_VND_MSEXCEL = "application/vnd.ms-excel";
    public static final String MIME_APPLICATION_VND_MSEXCEL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final Class<? extends ExcelMapper<T>> excelMapperType;

    public XLSParserService(Class<? extends ExcelMapper<T>> excelMapperType) {
        this.excelMapperType = excelMapperType;
    }

    public List<T> parse(File file) {
        try {
            return parse(file.getFile(), file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения содержимого документа: " + e.getMessage());
        }
    }

    public List<T> parse(byte[] file, String contentType) {
        final var result = new ArrayList<T>();

        if (file.length == 0)
            return result;

        try (final var inputStream = new ByteArrayInputStream(file);
             final var workbook = switch (contentType) {
                 case MIME_APPLICATION_VND_MSEXCEL -> new HSSFWorkbook(inputStream);
                 case MIME_APPLICATION_VND_MSEXCEL_2007 -> new XSSFWorkbook(inputStream);
                 default -> throw new RuntimeException("Не определен тип документа");
             }) {
            final var sheetIndex = 0;
            final var sheet = getSheetByIndex(workbook, sheetIndex);
            final var firstRowNum = RowUtils.getFirstNonEmptyRowNum(sheet);
            final var lastRowNum = sheet.getLastRowNum();
            final var headerRow = sheet.getRow(firstRowNum);

            if (firstRowNum == -1)
                return result;

            final var excelMapper = excelMapperType.getDeclaredConstructor(Row.class).newInstance(headerRow);

            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                final var row = sheet.getRow(rowNum);
                if (row != null) {
                    final var object = excelMapper.map(row);
                    result.add(object);
                }
            }
            return result;
        } catch (Exception e) {
            final var exceptionMessage = e instanceof InvocationTargetException invocationTargetException ?
                    invocationTargetException.getTargetException().getMessage() :
                    e.getMessage();
            throw new RuntimeException("Ошибка обработки документа: " + exceptionMessage);
        }
    }

    private static Sheet getSheetByIndex(Workbook workbook, int sheetIndex) {
        final var numberOfSheets = workbook.getNumberOfSheets();
        if (numberOfSheets == 0 || sheetIndex > numberOfSheets - 1)
            throw new RuntimeException("Ошибка обработки документа: отсутствуют листы");
        return workbook.getSheetAt(sheetIndex);
    }
}
