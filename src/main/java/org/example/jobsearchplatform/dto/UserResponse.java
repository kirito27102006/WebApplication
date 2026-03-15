package org.example.jobsearchplatform.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private Integer resumesCount;
    private List<SkillResponse> skills;
}