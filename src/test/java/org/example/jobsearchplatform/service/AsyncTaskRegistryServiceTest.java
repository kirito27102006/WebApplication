package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.AsyncTaskStatusResponse;
import org.example.jobsearchplatform.model.enums.AsyncTaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsyncTaskRegistryServiceTest {

    private AsyncTaskRegistryService asyncTaskRegistryService;

    @BeforeEach
    void setUp() {
        asyncTaskRegistryService = new AsyncTaskRegistryService();
    }

    @Test
    void createTask_returnsPendingTask() {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();

        assertNotNull(taskState.getTaskId());
        assertEquals(AsyncTaskStatus.PENDING, taskState.getStatus());
        assertEquals("Task accepted for asynchronous execution", taskState.getMessage());
        assertNotNull(taskState.getCreatedAt());
    }

    @Test
    void markCompleted_updatesTaskStatus() {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();

        asyncTaskRegistryService.markRunning(taskState.getTaskId());
        asyncTaskRegistryService.markCompleted(taskState.getTaskId());

        AsyncTaskStatusResponse response = asyncTaskRegistryService.getTaskStatus(taskState.getTaskId());
        assertEquals(AsyncTaskStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getStartedAt());
        assertNotNull(response.getCompletedAt());
        assertEquals("Task completed successfully", response.getMessage());
    }

    @Test
    void getTaskStatus_unknownTask_throws() {
        assertThrows(EntityNotFoundException.class,
                () -> asyncTaskRegistryService.getTaskStatus("missing-task-id"));
    }
}
