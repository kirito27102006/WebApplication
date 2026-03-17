package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.enums.VacancyStatus;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.ApplicationRepository;
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
    private final EmployerRepository employerRepository;
    private final ApplicationRepository applicationRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyResponse createVacancy(VacancyCreateRequest request) {
        Employer createdBy = employerRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new EntityNotFoundException("Employer not found " +
                        "with id: " + request.getCreatedById()));

        Vacancy vacancy = vacancyMapper.toEntity(request, createdBy);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toResponse(savedVacancy);
    }

    public VacancyResponse findById(Long id) {
        Vacancy vacancy = vacancyRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));
        return vacancyMapper.toResponse(vacancy);
    }

    public List<VacancyResponse> findAll() {
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

        Employer createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = employerRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("Employer not " +
                            "found with id: " + request.getCreatedById()));
        }

        vacancy.setTitle(request.getTitle());
        vacancy.setDescription(request.getDescription());
        vacancy.setSalary(request.getSalary());
        vacancy.setRequiredExperience(request.getRequiredExperience());
        vacancy.setLocation(request.getLocation());
        vacancy.setCreatedBy(createdBy);

        return vacancyMapper.toResponse(vacancy);
    }

    public void closeVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));
        vacancy.setStatus(VacancyStatus.CLOSED);
    }

    public void deleteVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));

        if (applicationRepository.existsByVacancyId(id)) {
            throw new IllegalStateException("Cannot delete vacancy with existing applications");
        }

        vacancyRepository.delete(vacancy);
    }
}