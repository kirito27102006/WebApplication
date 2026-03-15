package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.EmployerCreateRequest;
import org.example.jobsearchplatform.dto.EmployerResponse;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.service.mapper.EmployerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;
    private final EmployerMapper employerMapper;

    public EmployerResponse createEmployer(EmployerCreateRequest request) {
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employer with email " + request.getEmail() + " already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));

        Employer employer = employerMapper.toEntity(request, company);
        Employer savedEmployer = employerRepository.save(employer);
        return employerMapper.toResponse(savedEmployer);
    }

    public EmployerResponse findById(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + id));
        return employerMapper.toResponse(employer);
    }

    public EmployerResponse findByEmail(String email) {
        Employer employer = employerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found with email: " + email));
        return employerMapper.toResponse(employer);
    }

    public List<EmployerResponse> findAll() {
        return employerRepository.findAll().stream()
                .map(employerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<EmployerResponse> findByCompany(Long companyId) {
        return employerRepository.findByCompanyId(companyId).stream()
                .map(employerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EmployerResponse updateEmployer(Long id, EmployerCreateRequest request) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + id));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));

        employer.setFirstName(request.getFirstName());
        employer.setLastName(request.getLastName());
        employer.setPhoneNumber(request.getPhoneNumber());
        employer.setCompany(company);

        Employer updatedEmployer = employerRepository.save(employer);
        return employerMapper.toResponse(updatedEmployer);
    }

    public void blockEmployer(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + id));
        employer.setStatus("BLOCKED");
        employerRepository.save(employer);
    }

    public void deleteEmployer(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + id));
        employer.setStatus("DELETED");
        employerRepository.save(employer);
    }
}