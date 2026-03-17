package org.example.jobsearchplatform.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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