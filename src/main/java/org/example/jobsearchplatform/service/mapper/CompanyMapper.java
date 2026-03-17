package org.example.jobsearchplatform.service.mapper;

import org.example.jobsearchplatform.dto.CompanyCreateRequest;
import org.example.jobsearchplatform.dto.CompanyResponse;
import org.example.jobsearchplatform.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company toEntity(CompanyCreateRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setIndustry(request.getIndustry());
        company.setLocation(request.getLocation());
        company.setWebsite(request.getWebsite());
        company.setContactEmail(request.getContactEmail());
        company.setContactPhone(request.getContactPhone());
        return company;
    }

    public CompanyResponse toResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setDescription(company.getDescription());
        response.setIndustry(company.getIndustry());
        response.setLocation(company.getLocation());
        response.setWebsite(company.getWebsite());
        response.setContactEmail(company.getContactEmail());
        response.setContactPhone(company.getContactPhone());
        response.setCreatedAt(company.getCreatedAt());
        response.setEmployersCount(company.getEmployers().size());
        return response;
    }
}