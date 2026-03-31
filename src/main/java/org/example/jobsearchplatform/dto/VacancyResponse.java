package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Vacancy response DTO")
public class VacancyResponse {
    private Long id;
    private String title;
    private String description;
    private Integer salary;
    private Integer requiredExperience;
    private String location;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long companyId;
    private String companyName;
    private Long createdById;
    private String createdByName;
}
