package org.example.jobsearchplatform.service.mapper;

import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.springframework.stereotype.Component;

@Component
public class VacancyMapper {
    public Vacancy toEntity(VacancyCreateRequest request) {
        Vacancy vacancy = new Vacancy();
        vacancy.setJob(request.getJob());
        vacancy.setSalary(request.getSalary());
        vacancy.setExperience(request.getExperience());
        return vacancy;
    }

    public VacancyResponse toResponse(Vacancy vacancy) {
        VacancyResponse response = new VacancyResponse();
        response.setId(vacancy.getId());
        response.setJob(vacancy.getJob());
        response.setSalary(vacancy.getSalary());
        response.setExperience(vacancy.getExperience());
        return response;
    }
}
