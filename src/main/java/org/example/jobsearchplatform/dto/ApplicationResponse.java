package org.example.jobsearchplatform.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long vacancyId;
    private String vacancyTitle;
    private Long resumeId;
    private String resumeTitle;
    private String coverLetter;
    private String status;
    private LocalDateTime createdAt;
}