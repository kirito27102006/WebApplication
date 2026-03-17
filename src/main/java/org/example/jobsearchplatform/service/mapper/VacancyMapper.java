package org.example.jobsearchplatform.service.mapper;

import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.Employer;
import org.springframework.stereotype.Component;

@Component
public class VacancyMapper {

    public Vacancy toEntity(VacancyCreateRequest request, Employer createdBy) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle());
        vacancy.setDescription(request.getDescription());
        vacancy.setSalary(request.getSalary());
        vacancy.setRequiredExperience(request.getRequiredExperience());
        vacancy.setLocation(request.getLocation());
        vacancy.setCreatedBy(createdBy);
        return vacancy;
    }

    public VacancyResponse toResponse(Vacancy vacancy) {
        VacancyResponse response = new VacancyResponse();
        response.setId(vacancy.getId());
        response.setTitle(vacancy.getTitle());
        response.setDescription(vacancy.getDescription());
        response.setSalary(vacancy.getSalary());
        response.setRequiredExperience(vacancy.getRequiredExperience());
        response.setLocation(vacancy.getLocation());
        response.setStatus(vacancy.getStatus().name());
        response.setCreatedAt(vacancy.getCreatedAt());
        response.setUpdatedAt(vacancy.getUpdatedAt());

        if (vacancy.getCreatedBy() != null) {
            response.setCreatedById(vacancy.getCreatedBy().getId());
            response.setCreatedByName(vacancy.getCreatedBy().getFirstName() + " " +
                    vacancy.getCreatedBy().getLastName());

            if (vacancy.getCreatedBy().getCompany() != null) {
                response.setCompanyId(vacancy.getCreatedBy().getCompany().getId());
                response.setCompanyName(vacancy.getCreatedBy().getCompany().getName());
            }
        }

        return response;
    }
}