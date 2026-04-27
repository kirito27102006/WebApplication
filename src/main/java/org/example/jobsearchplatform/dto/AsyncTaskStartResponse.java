package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after async task submission")
public class AsyncTaskStartResponse {

    @Schema(example = "8f69f51b-62aa-4c5a-9092-842a9e4d3f0f")
    private String taskId;

    @Schema(example = "PENDING")
    private String status;

    @Schema(example = "Task accepted for asynchronous execution")
    private String message;

    private LocalDateTime timestamp;
}
