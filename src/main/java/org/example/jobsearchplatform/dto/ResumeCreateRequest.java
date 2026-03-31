package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating or updating a resume")
public class ResumeCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Schema(example = "Java Backend Developer")
    private String title;

    @Size(max = 2000, message = "Skills must not exceed 2000 characters")
    @Schema(example = "Java, Spring Boot, PostgreSQL")
    private String skills;

    @Size(max = 2000, message = "Experience must not exceed 2000 characters")
    @Schema(example = "5 years in backend development")
    private String experience;

    @Size(max = 2000, message = "Education must not exceed 2000 characters")
    @Schema(example = "BS in Computer Science")
    private String education;

    @PositiveOrZero(message = "Expected salary must be zero or positive")
    @Schema(example = "2500")
    private Integer expectedSalary;

    @Size(max = 50, message = "Location must not exceed 50 characters")
    @Schema(example = "Minsk")
    private String location;

    @NotNull(message = "User ID is required")
    @Schema(example = "1")
    private Long userId;
}
