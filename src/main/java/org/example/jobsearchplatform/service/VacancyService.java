package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.enums.VacancyStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.VacancyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
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
        Employer createdBy = getEmployerById(request.getCreatedById());
        Vacancy vacancy = vacancyMapper.toEntity(request, createdBy);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toResponse(savedVacancy);
    }

    @Transactional(readOnly = true)
    public VacancyResponse findById(Long id) {
        Vacancy vacancy = vacancyRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));
        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> findAll() {
        return vacancyRepository.findAllWithJoins().stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> searchVacancies(String title, String location,
                                                 Integer minSalary, Integer maxExperience) {
        return vacancyRepository.searchVacanciesWithJoins(title, location, minSalary, maxExperience)
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> findByCompany(Long companyId) {
        return vacancyRepository.findByCompanyIdWithJoins(companyId).stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public VacancyResponse updateVacancy(Long id, VacancyCreateRequest request) {
        Vacancy vacancy = getVacancyById(id);
        Employer createdBy = Optional.ofNullable(request.getCreatedById())
                .map(this::getEmployerById)
                .orElse(null);

        vacancy.setTitle(request.getTitle());
        vacancy.setDescription(request.getDescription());
        vacancy.setSalary(request.getSalary());
        vacancy.setRequiredExperience(request.getRequiredExperience());
        vacancy.setLocation(request.getLocation());
        vacancy.setCreatedBy(createdBy);

        return vacancyMapper.toResponse(vacancy);
    }

    public void closeVacancy(Long id) {
        getVacancyById(id).setStatus(VacancyStatus.CLOSED);
    }

    public void deleteVacancy(Long id) {
        Vacancy vacancy = getVacancyById(id);

        if (applicationRepository.existsByVacancyId(id)) {
            throw new IllegalStateException("Cannot delete vacancy with existing applications");
        }

        vacancyRepository.delete(vacancy);
    }

    @Transactional(readOnly = true)
    public Map<String, String> demonstrateNPlusOneProblem(Long id) {
        log.info("========== N+1 PROBLEM DEMO ==========");
        log.info("1. Query without fetch join (vacancyRepository.findById())");

        Vacancy badVacancy = getVacancyById(id);

        log.info("   Vacancy loaded: {}", badVacancy.getTitle());

        String companyName = badVacancy.getCreatedBy().getCompany().getName();
        log.info("   Separate query for company: {}", companyName);

        String creatorName = badVacancy.getCreatedBy().getFirstName();
        log.info("   Separate query for creator: {}", creatorName);

        log.info("   Total: 1 vacancy query + N related entity queries");
        log.info("========== Demo finished ==========");

        Map<String, String> response = new HashMap<>();
        response.put("message", "N+1 problem demo completed. Check the application logs and SQL query count.");
        response.put("vacancyTitle", badVacancy.getTitle());
        response.put("companyName", companyName);
        response.put("creatorName", creatorName);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, String> demonstrateNPlusOneSolution(Long id) {
        log.info("========== N+1 SOLUTION DEMO ==========");
        log.info("2. Query with FETCH JOIN (vacancyRepository.findByIdWithJoins())");

        Vacancy goodVacancy = vacancyRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));

        log.info("   Vacancy loaded: {}", goodVacancy.getTitle());

        String companyName = goodVacancy.getCreatedBy().getCompany().getName();
        log.info("   Company loaded via FETCH JOIN: {}", companyName);

        String creatorName = goodVacancy.getCreatedBy().getFirstName();
        log.info("   Creator loaded via FETCH JOIN: {}", creatorName);

        log.info("   Total: 1 database query");
        log.info("========== Demo finished ==========");

        Map<String, String> response = new HashMap<>();
        response.put("message", "N+1 solution demo completed. Check the application logs and SQL query count.");
        response.put("vacancyTitle", goodVacancy.getTitle());
        response.put("companyName", companyName);
        response.put("creatorName", creatorName);
        return response;
    }

    private Vacancy getVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));
    }

    private Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employer not found with id: " + id));
    }
}
