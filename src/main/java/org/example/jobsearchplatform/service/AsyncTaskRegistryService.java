package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.AsyncTaskStatusResponse;
import org.example.jobsearchplatform.model.enums.AsyncTaskStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncTaskRegistryService {

    private final Map<String, AsyncTaskState> tasks = new ConcurrentHashMap<>();

    public AsyncTaskState createTask() {
        String taskId = UUID.randomUUID().toString();
        AsyncTaskState taskState = AsyncTaskState.builder()
                .taskId(taskId)
                .status(AsyncTaskStatus.PENDING)
                .message("Task accepted for asynchronous execution")
                .createdAt(LocalDateTime.now())
                .build();
        tasks.put(taskId, taskState);
        return taskState;
    }

    public void markRunning(String taskId) {
        updateTask(taskId, current -> current.toBuilder()
                .status(AsyncTaskStatus.PROCESSING)
                .message("Task is currently being processed")
                .startedAt(LocalDateTime.now())
                .build());
    }

    public void markCompleted(String taskId) {
        updateTask(taskId, current -> current.toBuilder()
                .status(AsyncTaskStatus.COMPLETED)
                .message("Task completed successfully")
                .completedAt(LocalDateTime.now())
                .error(null)
                .build());
    }

    public void markFailed(String taskId, String errorMessage) {
        updateTask(taskId, current -> current.toBuilder()
                .status(AsyncTaskStatus.FAILED)
                .message("Task finished with error")
                .error(errorMessage)
                .completedAt(LocalDateTime.now())
                .build());
    }

    public AsyncTaskStatusResponse getTaskStatus(String taskId) {
        AsyncTaskState taskState = tasks.get(taskId);
        if (taskState == null) {
            throw new EntityNotFoundException("Async task with id '" + taskId + "' was not found");
        }
        return AsyncTaskStatusResponse.builder()
                .taskId(taskState.getTaskId())
                .status(taskState.getStatus())
                .message(taskState.getMessage())
                .error(taskState.getError())
                .createdAt(taskState.getCreatedAt())
                .startedAt(taskState.getStartedAt())
                .completedAt(taskState.getCompletedAt())
                .build();
    }

    public void removeTask(String taskId) {
        tasks.remove(taskId);
    }

    private void updateTask(String taskId, java.util.function.UnaryOperator<AsyncTaskState> updater) {
        tasks.compute(taskId, (key, current) -> {
            if (current == null) {
                throw new EntityNotFoundException("Async task with id '" + taskId + "' was not found");
            }
            return updater.apply(current);
        });
    }
}
