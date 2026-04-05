package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.EmployerCreateRequest;
import org.example.jobsearchplatform.dto.EmployerResponse;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.enums.EmployerStatus;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
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
    private final VacancyRepository vacancyRepository;
    private final EmployerMapper employerMapper;

    public EmployerResponse createEmployer(EmployerCreateRequest request) {
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Employer with email " + request.getEmail() + " already exists");
        }

        Company company = getCompanyById(request.getCompanyId());
        Employer employer = employerMapper.toEntity(request, company);
        Employer savedEmployer = employerRepository.save(employer);
        return employerMapper.toResponse(savedEmployer);
    }

    public EmployerResponse findById(Long id) {
        return employerMapper.toResponse(getEmployerById(id));
    }

    public EmployerResponse findByEmail(String email) {
        Employer employer = employerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employer not found with email: " + email));
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
        Employer employer = getEmployerById(id);
        Company company = getCompanyById(request.getCompanyId());

        employer.setFirstName(request.getFirstName());
        employer.setLastName(request.getLastName());
        employer.setPhoneNumber(request.getPhoneNumber());
        employer.setCompany(company);

        Employer updatedEmployer = employerRepository.save(employer);
        return employerMapper.toResponse(updatedEmployer);
    }

    public void blockEmployer(Long id) {
        getEmployerById(id).setStatus(EmployerStatus.BLOCKED);
    }

    public void deleteEmployer(Long id) {
        Employer employer = getEmployerById(id);

        if (vacancyRepository.existsByCreatedById(id)) {
            throw new IllegalStateException("Cannot delete employer with existing vacancies");
        }

        employerRepository.delete(employer);
    }

    private Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYER_NOT_FOUND_ID + id));
    }

    private Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
    }
}
