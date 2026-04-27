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
@Schema(description = "Thread-safe counter response")
public class CounterResponse {

    @Schema(example = "42")
    private long value;

    @Schema(example = "Counter incremented successfully")
    private String message;

    private LocalDateTime timestamp;
}
