package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.AsyncTaskStartResponse;
import org.example.jobsearchplatform.dto.AsyncTaskStatusResponse;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AsyncDemoTaskService {

    private static final String SUBMITTED_STATUS = "SUBMITTED";

    private final AsyncTaskRegistryService asyncTaskRegistryService;
    private final AsyncDemoTaskExecutor asyncDemoTaskExecutor;

    public AsyncTaskStartResponse startTask(DemoRequest request) {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();
        submitTask(taskState, () -> asyncDemoTaskExecutor.execute(taskState.getTaskId(), request));
        return buildStartResponse(taskState);
    }

    public AsyncTaskStartResponse startTaskWithoutDelay(DemoRequest request) {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();
        submitTask(taskState, () -> asyncDemoTaskExecutor.executeWithoutDelay(taskState.getTaskId(), request));
        return buildStartResponse(taskState);
    }

    private void submitTask(AsyncTaskState taskState, Runnable taskSubmission) {
        try {
            taskSubmission.run();
        } catch (TaskRejectedException ex) {
            asyncTaskRegistryService.removeTask(taskState.getTaskId());
            throw new TaskRejectedException("Async task queue is full. Please retry later.", ex);
        }
    }

    private AsyncTaskStartResponse buildStartResponse(AsyncTaskState taskState) {
        return AsyncTaskStartResponse.builder()
                .taskId(taskState.getTaskId())
                .status(SUBMITTED_STATUS)
                .message(taskState.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public AsyncTaskStatusResponse getTaskStatus(String taskId) {
        return asyncTaskRegistryService.getTaskStatus(taskId);
    }
}
