package main.service.asyns_task;

import main.model.Task;
import main.model.dictionaries.TaskStatus;
import main.model.dictionaries.TaskType;
import main.repository.TaskStatusRepository;
import org.apache.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AsyncTaskService<T> {
    private static final Logger log = Logger.getLogger(AsyncTaskService.class.getName());
    private final Consumer<T> task;
    private final TaskStatusRepository taskStatusRepository;

    protected AsyncTaskService(TaskStatusRepository taskStatusRepository, Consumer<T> task) {
        this.task = task;
        this.taskStatusRepository = taskStatusRepository;
    }

    public void executeAsyncTask(UUID taskId, TaskType taskType, T param) {
        CompletableFuture.runAsync(() -> {
            final var task = new Task(taskId, TaskStatus.IN_PROGRESS, taskType);
            taskStatusRepository.save(task);
            try {
                this.task.accept(param);
                updateTaskStatus(task, TaskStatus.DONE);
            }
            catch (Exception e) {
                log.error(e.getMessage());
                updateTaskStatus(task, TaskStatus.ERROR);
            }
        });
    }

    public TaskStatus getTaskStatus(UUID taskId) {
        return taskStatusRepository.findById(taskId).map(Task::getTaskStatus).orElse(null);
    }

    private void updateTaskStatus(Task task, TaskStatus taskStatus) {
        task.setTaskStatus(taskStatus);
        taskStatusRepository.save(task);
    }
}
