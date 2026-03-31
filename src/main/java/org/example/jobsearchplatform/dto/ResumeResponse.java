package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Resume response DTO")
public class ResumeResponse {
    private Long id;
    private String title;
    private String skills;
    private String experience;
    private String education;
    private Integer expectedSalary;
    private String location;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userFullName;
}
