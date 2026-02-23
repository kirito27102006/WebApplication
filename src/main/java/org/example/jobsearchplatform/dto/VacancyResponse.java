package org.example.jobsearchplatform.dto;

import lombok.Data;

@Data
public class VacancyResponse {
    private String job;
    private int salary;
    private int experience;
    private Long id;
}
