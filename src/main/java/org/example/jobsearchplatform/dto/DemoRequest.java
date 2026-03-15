package org.example.jobsearchplatform.dto;

import lombok.Data;

@Data
public class DemoRequest {
    private String companyName;
    private String employerEmail;
    private String employerFirstName;
    private String employerLastName;
}