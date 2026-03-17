package org.example.jobsearchplatform.service.mapper;

import org.example.jobsearchplatform.dto.EmployerCreateRequest;
import org.example.jobsearchplatform.dto.EmployerResponse;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.Company;
import org.springframework.stereotype.Component;

@Component
public class EmployerMapper {

    public Employer toEntity(EmployerCreateRequest request, Company company) {
        Employer employer = new Employer();
        employer.setFirstName(request.getFirstName());
        employer.setLastName(request.getLastName());
        employer.setEmail(request.getEmail());
        employer.setPhoneNumber(request.getPhoneNumber());
        employer.setCompany(company);
        return employer;
    }

    public EmployerResponse toResponse(Employer employer) {
        EmployerResponse response = new EmployerResponse();
        response.setId(employer.getId());
        response.setFirstName(employer.getFirstName());
        response.setLastName(employer.getLastName());
        response.setEmail(employer.getEmail());
        response.setPhoneNumber(employer.getPhoneNumber());
        response.setStatus(employer.getStatus().name());
        response.setCreatedAt(employer.getCreatedAt());

        if (employer.getCompany() != null) {
            response.setCompanyId(employer.getCompany().getId());
            response.setCompanyName(employer.getCompany().getName());
        }

        response.setVacanciesCount(employer.getCreatedVacancies().size());

        return response;
    }
}