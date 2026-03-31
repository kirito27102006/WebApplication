package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating or updating a company")
public class CompanyCreateRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    @Schema(example = "Tech Corp")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(example = "International software company")
    private String description;

    @Size(max = 100, message = "Industry must not exceed 100 characters")
    @Schema(example = "Information Technology")
    private String industry;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Schema(example = "Minsk")
    private String location;

    @Pattern(regexp = "^(https?://)?[\\w\\-.]+\\.[a-z]{2,}$", message = "Invalid website URL")
    @Schema(example = "https://techcorp.com")
    private String website;

    @Email(message = "Invalid email format")
    @Schema(example = "hr@techcorp.com")
    private String contactEmail;

    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Invalid phone number")
    @Schema(example = "+375291112233")
    private String contactPhone;
}
