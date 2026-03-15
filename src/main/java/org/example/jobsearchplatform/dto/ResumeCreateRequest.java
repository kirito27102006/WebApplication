package org.example.jobsearchplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 2000, message = "Skills must not exceed 2000 characters")
    private String skills;

    @Size(max = 2000, message = "Experience must not exceed 2000 characters")
    private String experience;

    @Size(max = 2000, message = "Education must not exceed 2000 characters")
    private String education;

    @PositiveOrZero(message = "Expected salary must be zero or positive")
    private Integer expectedSalary;

    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;

    @NotNull(message = "User ID is required")
    private Long userId;
}