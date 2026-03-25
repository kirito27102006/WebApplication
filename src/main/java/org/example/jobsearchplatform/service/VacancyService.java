package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Employer createdBy = employerRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new EntityNotFoundException("Employer n" +
                        "ot found with id: " + request.getCreatedById()));

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
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_NOT_FOUND + id));

        Employer createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = employerRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("Employe" +
                            "r not found with id: " + request.getCreatedById()));
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

    @Transactional(readOnly = true)
    public Map<String, String> demonstrateNPlusOneProblem(Long id) {
        log.info("========== ДЕМОНСТРАЦИЯ ПРОБЛЕМЫ N+1 ==========");
        log.info("1. Запрос БЕЗ fetch join (вызов vacancyRepository.findById()):");

        Vacancy badVacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));

        log.info("   ✅ Загружена вакансия: {}", badVacancy.getTitle());

        String companyName = badVacancy.getCreatedBy().getCompany().getName();
        log.info("   🔄 Отдельный запрос к компании: {}", companyName);

        String creatorName = badVacancy.getCreatedBy().getFirstName();
        log.info("   🔄 Отдельный запрос к создателю: {}", creatorName);

        log.info("   📊 ИТОГО: 1 (вакансия) + N (связанные сущности) запросов к БД");
        log.info("========== Демонстрация завершена ==========");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Демонстрация проблемы N+1 выполнена. Проверьте ло" +
                "ги приложения и количество SQL-запросов.");
        response.put("vacancyTitle", badVacancy.getTitle());
        response.put("companyName", companyName);
        response.put("creatorName", creatorName);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, String> demonstrateNPlusOneSolution(Long id) {
        log.info("========== ДЕМОНСТРАЦИЯ РЕШЕНИЯ ПРОБЛЕМЫ N+1 ==========");
        log.info("2. Запрос С FETCH JOIN (вызов vacancyRepository.findByIdWithJoins()):");

        Vacancy goodVacancy = vacancyRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));

        log.info("   ✅ Загружена вакансия: {}", goodVacancy.getTitle());

        String companyName = goodVacancy.getCreatedBy().getCompany().getName();
        log.info("   ✅ Компания загружена через FETCH JOIN: {}", companyName);

        String creatorName = goodVacancy.getCreatedBy().getFirstName();
        log.info("   ✅ Создатель загружен через FETCH JOIN: {}", creatorName);

        log.info("   📊 ИТОГО: 1 запрос к БД (все данные получены за один раз)");
        log.info("========== Демонстрация завершена ==========");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Демонстрация решения проблемы N+1 выполнена. Про" +
                "верьте логи приложения и количество SQL-запросов.");
        response.put("vacancyTitle", goodVacancy.getTitle());
        response.put("companyName", companyName);
        response.put("creatorName", creatorName);
        return response;
    }
}