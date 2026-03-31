package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating or updating an employer")
public class EmployerCreateRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(example = "Anna")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(example = "Smith")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "anna.smith@company.com")
    private String email;

    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Invalid phone number")
    @Schema(example = "+375291112233")
    private String phoneNumber;

    @NotNull(message = "Company ID is required")
    @Schema(example = "2")
    private Long companyId;
}
