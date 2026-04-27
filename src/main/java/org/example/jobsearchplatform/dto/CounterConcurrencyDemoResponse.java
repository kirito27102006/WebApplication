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
@Schema(description = "Single counter concurrency demo response")
public class CounterConcurrencyDemoResponse {

    @Schema(example = "unsafe")
    private String strategy;

    @Schema(example = "64")
    private int threadCount;

    @Schema(example = "2000")
    private int incrementsPerThread;

    @Schema(example = "128000")
    private long expectedValue;

    @Schema(example = "97543")
    private long actualValue;

    @Schema(example = "30457")
    private long lostUpdates;

    @Schema(example = "true")
    private boolean threadSafe;

    @Schema(example = "Counter lost updates under concurrent access")
    private String message;

    private LocalDateTime timestamp;
}
