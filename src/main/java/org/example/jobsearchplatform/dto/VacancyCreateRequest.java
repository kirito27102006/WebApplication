package org.example.jobsearchplatform.dto;

import lombok.Data;

@Data
public class VacancyCreateRequest {
    private String job;
    private int salary;
    private int experience;
}
