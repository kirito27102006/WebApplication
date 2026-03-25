package org.example.jobsearchplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationCreateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Vacancy ID is required")
    private Long vacancyId;

    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;
}