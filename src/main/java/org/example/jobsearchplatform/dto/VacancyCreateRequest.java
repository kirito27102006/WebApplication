package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating or updating a vacancy")
public class VacancyCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    @Schema(example = "Senior Java Developer")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(example = "Backend role with Spring Boot")
    private String description;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    @Schema(example = "3500")
    private Integer salary;

    @PositiveOrZero(message = "Experience must be zero or positive")
    @Schema(example = "3")
    private Integer requiredExperience;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Schema(example = "Remote")
    private String location;

    @NotNull(message = "Employer ID is required")
    @Schema(example = "4")
    private Long createdById;
}
