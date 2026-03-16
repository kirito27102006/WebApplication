package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.service.mapper.VacancyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VacancyService {

    private static final String VACANCY_NOT_FOUND = "Vacancy not found with id: ";

    private final VacancyRepository vacancyRepository;
    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyResponse createVacancy(VacancyCreateRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + request.getCompanyId()));

        Employer createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = employerRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("Employer not fo" +
                            "und with id: " + request.getCreatedById()));
        }

        Vacancy vacancy = vacancyMapper.toEntity(request, company, createdBy);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toResponse(savedVacancy);
    }

    public VacancyResponse findById(Long id) {
        // ИСПОЛЬЗУЕМ НОВЫЙ МЕТОД С JOIN FETCH
        Vacancy vacancy = vacancyRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));
        return vacancyMapper.toResponse(vacancy);
    }

    public List<VacancyResponse> findAll() {
        // ИСПОЛЬЗУЕМ НОВЫЙ МЕТОД С JOIN FETCH
        return vacancyRepository.findAllWithJoins().stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public List<VacancyResponse> searchVacancies(
            String title,
            String location,
            Integer minSalary,
            Integer maxExperience) {

        return vacancyRepository.searchVacanciesWithJoins(title, location, minSalary, maxExperience)
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public List<VacancyResponse> findByCompany(Long companyId) {
        return vacancyRepository.findByCompanyIdWithJoins(companyId).stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public VacancyResponse updateVacancy(Long id, VacancyCreateRequest request) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + request.getCompanyId()));

        Employer createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = employerRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("Employer not fo" +
                            "und with id: " + request.getCreatedById()));
        }

        vacancy.setTitle(request.getTitle());
        vacancy.setDescription(request.getDescription());
        vacancy.setSalary(request.getSalary());
        vacancy.setRequiredExperience(request.getRequiredExperience());
        vacancy.setLocation(request.getLocation());
        vacancy.setCompany(company);
        vacancy.setCreatedBy(createdBy);

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toResponse(updatedVacancy);
    }

    public void closeVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));
        vacancy.setStatus("CLOSED");
        vacancyRepository.save(vacancy);
    }

    public void deleteVacancy(Long id) {
        vacancyRepository.deleteById(id);
    }
}