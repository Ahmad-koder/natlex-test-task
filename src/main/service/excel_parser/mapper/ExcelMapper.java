package main.service.excel_parser.mapper;

import main.service.excel_parser.util.CellUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

public abstract class ExcelMapper<T> {
    private static Logger log = Logger.getLogger(ExcelMapper.class.getName());

    private final List<String> headerList = new ArrayList<>();
    private final Map<String, Integer> headerNameToCellIndexMap;

    public ExcelMapper(Row headerRow) {
        headerList.addAll(headerList(headerRow));
        this.headerNameToCellIndexMap = new HashMap<>();

        if (CollectionUtils.isEmpty(headerList)) {
            log.error("Не инициализиован список заголовков");
            throw new RuntimeException("Ошибка парсинга файла");
        }

        headerList.forEach(header -> this.headerNameToCellIndexMap.put(header, null));
        initHeadersCellNumber(headerRow);
        checkCellIndexes(this.headerNameToCellIndexMap);
    }

    @SuppressWarnings("unchecked")
    public <E> E getCellValue(Row row, String headerName, Class<E> valueType) {
        final var cellIndex = headerNameToCellIndexMap.get(headerName);
        final var cell = row.getCell(cellIndex);

        if (cell == null) {
            return null;
        }

        final var cellValueObj = CellUtils.getCellValue(cell);

        if (String.class.equals(valueType)) {
            return (E) String.valueOf(cellValueObj);
        }
        if (BigDecimal.class.equals(valueType) && cellValueObj instanceof Integer) {
            return (E) new BigDecimal((int) cellValueObj);
        }
        if (Integer.class.equals(valueType) && cellValueObj instanceof BigDecimal) {
            return (E) (Integer) ((BigDecimal) cellValueObj).intValue();
        }

        if (valueType.isInstance(cellValueObj)) {
            return (E) cellValueObj;
        } else {
            log.error("Неккоректные типы данных в ячейке");
            throw new RuntimeException("данные в ячейке " + cell.getAddress().formatAsString() + " имеют некорректный формат данных");
        }
    }

    public T map(Row row) {
        return null;
    }

    protected List<String> headerList(Row headerRow) {
        return Collections.emptyList();
    }

    private void initHeadersCellNumber(Row row) {
        for (Cell cell : row) {
            final var cellValueObj = CellUtils.getCellValue(cell);
            for (String header : headerList) {
                if (cellValueObj instanceof final String cellValueStr) {
                    if (cellValueStr.equalsIgnoreCase(header)) {
                        this.headerNameToCellIndexMap.put(cellValueStr, cell.getColumnIndex());
                    }
                } else {
                    log.error("В ячейках заголовка не строкове значение");
                    throw new RuntimeException("заголовок " + cell.getAddress().formatAsString() + "не является строкой");
                }
            }
        }
    }

    private static void checkCellIndexes(Map<String, Integer> headerNameToCellIndexMap) {
        final var missingHeaders = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : headerNameToCellIndexMap.entrySet()) {
            if (entry.getValue() == null) {
                missingHeaders.add(entry.getKey());
            }
        }

        if (missingHeaders.size() > 0) {
            log.error("Количество заголовков в файле не совпадает с заданным списком");
            throw new RuntimeException("отсутствуют обязательные заголовки " + missingHeaders);
        }
    }

    public List<String> getHeaderList() {
        return headerList;
    }
}
