package org.example.jobsearchplatform.service;

import lombok.Builder;
import lombok.Data;
import org.example.jobsearchplatform.model.enums.AsyncTaskStatus;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class AsyncTaskState {
    private String taskId;
    private AsyncTaskStatus status;
    private String message;
    private String error;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
