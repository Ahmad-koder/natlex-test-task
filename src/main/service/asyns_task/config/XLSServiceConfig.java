package main.service.asyns_task.config;

import main.model.File;
import main.repository.TaskStatusRepository;
import main.service.ImportExportXLSService;
import main.service.asyns_task.AsyncTaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class XLSServiceConfig {
    @Bean
    public AsyncTaskService<File> importXLSService(TaskStatusRepository taskStatusRepository,
                                                   ImportExportXLSService importExportXLSService) {
        return new AsyncTaskService<>(
                taskStatusRepository,
                importExportXLSService::importXLS
        ) {
        };
    }

    @Bean
    public AsyncTaskService<UUID> exportXLSService(TaskStatusRepository taskStatusRepository,
                                                   ImportExportXLSService importExportXLSService) {
        return new AsyncTaskService<>(
                taskStatusRepository,
                importExportXLSService::exportXLS
        ) {
        };
    }
}
