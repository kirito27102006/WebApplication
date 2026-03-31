package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Application response DTO")
public class ApplicationResponse {
    @Schema(example = "100")
    private Long id;
    @Schema(example = "1")
    private Long userId;
    @Schema(example = "John Doe")
    private String userFullName;
    @Schema(example = "10")
    private Long vacancyId;
    @Schema(example = "Java Developer")
    private String vacancyTitle;
    @Schema(example = "5")
    private Long resumeId;
    @Schema(example = "Senior Backend Resume")
    private String resumeTitle;
    private String coverLetter;
    @Schema(example = "PENDING")
    private String status;
    private LocalDateTime createdAt;
}
