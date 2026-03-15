package org.example.jobsearchplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VacancyCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private Integer salary;

    @PositiveOrZero(message = "Experience must be zero or positive")
    private Integer requiredExperience;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private Long createdById;
}