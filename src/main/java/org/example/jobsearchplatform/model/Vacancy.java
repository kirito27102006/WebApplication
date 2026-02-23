package org.example.jobsearchplatform.model;

import lombok.Data;

@Data
public class Vacancy {
    private String job;
    private int salary;
    private int experience;
    private Long id;
}
