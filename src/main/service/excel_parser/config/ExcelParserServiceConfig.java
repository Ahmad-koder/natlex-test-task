package main.service.excel_parser.config;

import main.model.Section;
import main.service.excel_parser.XLSParserService;
import main.service.excel_parser.mapper.SectionExcelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExcelParserServiceConfig {

    @Bean
    public XLSParserService<Section> autoCreateExcelParserService() {
        return new XLSParserService<>(SectionExcelMapper.class);
    }
}
