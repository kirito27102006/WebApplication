package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.jobsearchplatform.model.enums.AsyncTaskStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current status of async task execution")
public class AsyncTaskStatusResponse {

    @Schema(example = "8f69f51b-62aa-4c5a-9092-842a9e4d3f0f")
    private String taskId;

    private AsyncTaskStatus status;

    @Schema(example = "Task is currently being processed")
    private String message;

    @Schema(example = "Invalid email format: invalid-email")
    private String error;

    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
}
