package org.example.jobsearchplatform.service;

import org.example.jobsearchplatform.dto.AsyncTaskStartResponse;
import org.example.jobsearchplatform.dto.AsyncTaskStatusResponse;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.model.enums.AsyncTaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskRejectedException;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsyncDemoTaskServiceTest {

    private AsyncTaskRegistryService asyncTaskRegistryService;
    private FakeAsyncDemoTaskExecutor asyncDemoTaskExecutor;
    private AsyncDemoTaskService asyncDemoTaskService;

    @BeforeEach
    void setUp() {
        asyncTaskRegistryService = new AsyncTaskRegistryService();
        asyncDemoTaskExecutor = new FakeAsyncDemoTaskExecutor();
        asyncDemoTaskService = new AsyncDemoTaskService(asyncTaskRegistryService, asyncDemoTaskExecutor);
    }

    @Test
    void startTask_returnsTaskIdAndPendingStatus() {
        DemoRequest request = new DemoRequest();

        AsyncTaskStartResponse response = asyncDemoTaskService.startTask(request);

        assertNotNull(response.getTaskId());
        assertEquals("SUBMITTED", response.getStatus());
        assertEquals(response.getTaskId(), asyncDemoTaskExecutor.lastTaskId);
        assertEquals(request, asyncDemoTaskExecutor.lastRequest);
        assertEquals(false, asyncDemoTaskExecutor.withoutDelayUsed);
    }

    @Test
    void startTaskWithoutDelay_returnsTaskIdAndUsesFastExecutorPath() {
        DemoRequest request = new DemoRequest();

        AsyncTaskStartResponse response = asyncDemoTaskService.startTaskWithoutDelay(request);

        assertNotNull(response.getTaskId());
        assertEquals("SUBMITTED", response.getStatus());
        assertEquals(response.getTaskId(), asyncDemoTaskExecutor.lastTaskId);
        assertEquals(request, asyncDemoTaskExecutor.lastRequest);
        assertEquals(true, asyncDemoTaskExecutor.withoutDelayUsed);
    }

    @Test
    void getTaskStatus_returnsCurrentTaskState() {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();
        asyncTaskRegistryService.markRunning(taskState.getTaskId());

        AsyncTaskStatusResponse response = asyncDemoTaskService.getTaskStatus(taskState.getTaskId());

        assertEquals(taskState.getTaskId(), response.getTaskId());
        assertEquals(AsyncTaskStatus.PROCESSING, response.getStatus());
    }

    @Test
    void startTaskWithoutDelay_whenExecutorRejectsTask_throwsAndDoesNotLeaveTaskInRegistry() {
        asyncDemoTaskExecutor.rejectRequests = true;
        DemoRequest request = new DemoRequest();

        TaskRejectedException exception = assertThrows(
                TaskRejectedException.class,
                () -> asyncDemoTaskService.startTaskWithoutDelay(request));

        assertEquals("Async task queue is full. Please retry later.", exception.getMessage());
        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> asyncTaskRegistryService.getTaskStatus(asyncDemoTaskExecutor.lastTaskId));
    }

    private static class FakeAsyncDemoTaskExecutor extends AsyncDemoTaskExecutor {
        private String lastTaskId;
        private DemoRequest lastRequest;
        private boolean withoutDelayUsed;
        private boolean rejectRequests;

        FakeAsyncDemoTaskExecutor() {
            super(null, null, 0);
        }

        @Override
        public CompletableFuture<Void> execute(String taskId, DemoRequest request) {
            lastTaskId = taskId;
            if (rejectRequests) {
                throw new TaskRejectedException("Rejected");
            }
            lastTaskId = taskId;
            lastRequest = request;
            withoutDelayUsed = false;
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<Void> executeWithoutDelay(String taskId, DemoRequest request) {
            lastTaskId = taskId;
            if (rejectRequests) {
                throw new TaskRejectedException("Rejected");
            }
            lastTaskId = taskId;
            lastRequest = request;
            withoutDelayUsed = true;
            return CompletableFuture.completedFuture(null);
        }
    }
}
