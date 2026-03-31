package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating an application")
public class ApplicationCreateRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User identifier", example = "1")
    private Long userId;

    @NotNull(message = "Vacancy ID is required")
    @Schema(description = "Vacancy identifier", example = "10")
    private Long vacancyId;

    @NotNull(message = "Resume ID is required")
    @Schema(description = "Resume identifier", example = "5")
    private Long resumeId;

    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    @Schema(description = "Optional cover letter", example = "I am interested in this role")
    private String coverLetter;
}
