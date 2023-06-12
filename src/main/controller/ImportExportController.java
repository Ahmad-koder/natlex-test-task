package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.File;
import main.model.dictionaries.TaskStatus;
import main.model.dictionaries.TaskType;
import main.repository.FilesRepository;
import main.service.asyns_task.AsyncTaskService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/xls")
public class ImportExportController {
    private final AsyncTaskService<File> importTaskService;
    private final AsyncTaskService<UUID> exportTaskService;
    private final FilesRepository filesRepository;

    @PostMapping("/import")
    public ResponseEntity<UUID> importFile(@RequestParam("file") MultipartFile file) {
        try {
            final var taskId = UUID.randomUUID();
            importTaskService.executeAsyncTask(taskId, TaskType.IMPORT, new File(file.getBytes(), file.getContentType()));
            return ResponseEntity.ok(taskId);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла");
        }
    }

    @GetMapping("/import/{id}")
    public ResponseEntity<TaskStatus> getImportTaskStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(importTaskService.getTaskStatus(id));
    }

    @GetMapping("/export")
    public ResponseEntity<UUID> exportToFile() {
        final var taskId = UUID.randomUUID();
        exportTaskService.executeAsyncTask(taskId, TaskType.EXPORT, taskId);
        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<TaskStatus> getExportTaskStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(exportTaskService.getTaskStatus(id));
    }

    @GetMapping("/files/{id}")
    @Transactional
    public ResponseEntity<byte[]> getFile(@PathVariable UUID id) {
        final var taskStatus = exportTaskService.getTaskStatus(id);
        if (taskStatus.equals(TaskStatus.IN_PROGRESS))
            throw new RuntimeException("Файл в процессе формирования");

        final var file = filesRepository.findByTaskId(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getFile());
    }
}
