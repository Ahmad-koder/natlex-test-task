package main.service;

import lombok.RequiredArgsConstructor;
import main.model.File;
import main.model.Section;
import main.repository.FilesRepository;
import main.repository.SectionRepository;
import main.service.excel_parser.XLSParserService;
import main.service.excel_parser.mapper.SectionExcelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImportExportXLSService {
    private final XLSParserService<Section> sectionXLSParserService;
    private final SectionRepository sectionRepository;
    private final FilesRepository filesRepository;

    @Transactional
    public void importXLS(File file) {
        try {
            Thread.sleep(10000);
        }
        catch (Exception e) {
            throw new RuntimeException("sdsd");
        }
        List<Section> sectionList = sectionXLSParserService.parse(file);
        sectionRepository.saveAll(sectionList);
    }

    @Transactional
    public void exportXLS(UUID taskId) {
        try {
            Thread.sleep(10000);
        }
        catch (Exception e) {
            throw new RuntimeException("sdsd");
        }
        List<Section> sectionList = sectionRepository.findAll();
        byte[] xls = SectionExcelMapper.map(sectionList);
        filesRepository.save(new File(UUID.randomUUID(), xls, taskId, null, "Section.xls"));
    }
}
