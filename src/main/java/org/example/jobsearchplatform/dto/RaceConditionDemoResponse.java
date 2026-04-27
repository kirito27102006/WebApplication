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
@Schema(description = "Race condition demo response")
public class RaceConditionDemoResponse {

    @Schema(example = "64")
    private int threadCount;

    @Schema(example = "2000")
    private int incrementsPerThread;

    @Schema(example = "128000")
    private long expectedValue;

    @Schema(example = "97543")
    private long unsafeValue;

    @Schema(example = "128000")
    private long safeValue;

    @Schema(example = "30457")
    private long unsafeLostUpdates;

    @Schema(example = "0")
    private long safeLostUpdates;

    @Schema(example = "true")
    private boolean raceConditionDetected;

    @Schema(example = "Unsafe counter lost updates under concurrent access, AtomicLong solved the issue")
    private String message;

    private LocalDateTime timestamp;
}
