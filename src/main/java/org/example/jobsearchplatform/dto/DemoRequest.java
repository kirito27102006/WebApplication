package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for demo transaction endpoints")
public class DemoRequest {
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    @Schema(example = "Company_WithTx_1")
    private String companyName;

    @NotBlank(message = "Employer email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "demo.employer@example.com")
    private String employerEmail;

    @NotBlank(message = "Employer first name is required")
    @Size(min = 2, max = 50, message = "Employer first name must be between 2 and 50 characters")
    @Schema(example = "Demo")
    private String employerFirstName;

    @NotBlank(message = "Employer last name is required")
    @Size(min = 2, max = 50, message = "Employer last name must be between 2 and 50 characters")
    @Schema(example = "User")
    private String employerLastName;
}
