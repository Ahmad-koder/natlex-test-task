package main.service.excel_parser.mapper;

import main.model.GeologicalClass;
import main.model.Section;
import main.service.excel_parser.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SectionExcelMapper extends ExcelMapper<Section> {

    public SectionExcelMapper(Row headerRow) {
        super(headerRow);
    }

    @Override
    public Section map(Row row) {
        Section section = new Section();
        List<String> headers = getHeaderList();
        section.setName(getCellValue(row, headers.get(0), String.class));
        List<GeologicalClass> geologicalClasses = new ArrayList<>();
        for (int colIndex = 1; colIndex < headers.size(); colIndex += 2) {
            String className = getCellValue(row, headers.get(colIndex), String.class);
            String classCode = getCellValue(row, headers.get(colIndex + 1), String.class);
            if (className != null && classCode != null) {
                GeologicalClass geologicalClass = new GeologicalClass();
                geologicalClass.setName(className);
                geologicalClass.setCode(classCode);
                geologicalClasses.add(geologicalClass);
            }
        }
        section.setGeologicalClasses(geologicalClasses);
        return section;
    }

    public static byte[] map(List<Section> sections) {
        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sections");

            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>();
            headers.add("Section name");
            int countGeologicalClasses = sections.stream().map(section -> section.getGeologicalClasses().size())
                    .max(Comparator.comparingInt(size -> size)).stream().findFirst().orElse(0);
            for (int i = 0; i < countGeologicalClasses; i++) {
                headers.add("Class " + (i + 1) + " name");
                headers.add("Class " + (i + 1) + " code");
            }
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            for (int i = 0; i < sections.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                Section section = sections.get(i);
                dataRow.createCell(0).setCellValue(section.getName());
                for (GeologicalClass geologicalClass : section.getGeologicalClasses()) {
                    int columnNumber = StringUtils.getLastDigit(geologicalClass.getName());
                    if (columnNumber != 1) {
                        dataRow.createCell(columnNumber * 2 - 1).setCellValue(geologicalClass.getName());
                        dataRow.createCell(columnNumber * 2).setCellValue(geologicalClass.getCode());
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка формирования файла");
        }
    }

    @Override
    protected List<String> headerList(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }
        return headers;
    }
}
