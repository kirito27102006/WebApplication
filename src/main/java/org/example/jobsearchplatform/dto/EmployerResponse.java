package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Employer response DTO")
public class EmployerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private Long companyId;
    private String companyName;
    private Integer vacanciesCount;
}
