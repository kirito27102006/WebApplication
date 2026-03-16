package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
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

@Service
@RequiredArgsConstructor
@Transactional
public class EmployerService {

    private static final String EMPLOYER_NOT_FOUND_ID = "Employer not found with id: ";

    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;
    private final EmployerMapper employerMapper;

    public EmployerResponse createEmployer(EmployerCreateRequest request) {
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Employer with email " + request.getEmail() + " already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));

        Employer employer = employerMapper.toEntity(request, company);
        Employer savedEmployer = employerRepository.save(employer);
        return employerMapper.toResponse(savedEmployer);
    }

    public EmployerResponse findById(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYER_NOT_FOUND_ID + id));
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
                .toList();
    }

    public List<EmployerResponse> findByCompany(Long companyId) {
        return employerRepository.findByCompanyId(companyId).stream()
                .map(employerMapper::toResponse)
                .toList();
    }

    public EmployerResponse updateEmployer(Long id, EmployerCreateRequest request) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYER_NOT_FOUND_ID + id));

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
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYER_NOT_FOUND_ID + id));
        employer.setStatus("BLOCKED");
        employerRepository.save(employer);
    }

    public void deleteEmployer(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYER_NOT_FOUND_ID + id));
        employer.setStatus("DELETED");
        employerRepository.save(employer);
    }
}