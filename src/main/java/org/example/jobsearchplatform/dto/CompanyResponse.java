package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Company response DTO")
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private String industry;
    private String location;
    private String website;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;
    private Integer employersCount;
}
