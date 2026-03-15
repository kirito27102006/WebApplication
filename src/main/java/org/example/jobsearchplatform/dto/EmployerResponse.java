package org.example.jobsearchplatform.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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