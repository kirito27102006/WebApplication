package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.VacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final VacancyRepository vacancyRepository;

    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponse> getVacancyById(@PathVariable Long id) {
        VacancyResponse response = vacancyService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VacancyResponse>> getAllVacancies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Long companyId) {

        if (companyId != null) {
            return ResponseEntity.ok(vacancyService.findByCompany(companyId));
        } else if (title != null || location != null || minSalary != null || maxExperience != null) {
            return ResponseEntity.ok(vacancyService.searchVacancies(title, location, minSalary, maxExperience));
        }

        return ResponseEntity.ok(vacancyService.findAll());
    }


    @GetMapping("/demo/nplus1/{id}")
    public ResponseEntity<Map<String, String>> demonstrateNPlusOneProblem(@PathVariable Long id) {
        log.info("========== ДЕМОНСТРАЦИЯ ПРОБЛЕМЫ N+1 ==========");
        log.info("1. Запрос БЕЗ fetch join (вызов vacancyRepository.findById()):");


        Vacancy badVacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Vacancy not found"));

        log.info("   ✅ Загружена вакансия: {}", badVacancy.getTitle());

        // Теперь company доступна через createdBy
        String companyName = badVacancy.getCreatedBy().getCompany().getName();
        log.info("   🔄 Отдельный запрос к компании (LazyInitializationException " +
                "не будет, т.к. мы в транзакции? но запрос будет): {}", companyName);

        if (badVacancy.getCreatedBy() != null) {
            String creatorName = badVacancy.getCreatedBy().getFirstName();
            log.info("   🔄 Отдельный запрос к создателю: {}", creatorName);
        }

        log.info("   📊 ИТОГО: 1 (вакансия) + N (связанные сущности) запросов к БД");
        log.info("========== Демонстрация завершена ==========");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Демонстрация проблемы N+1 выполнена. Проверьте " +
                "логи приложения и количество SQL-запросов.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/demo/solution/{id}")
    public ResponseEntity<Map<String, String>> demonstrateNPlusOneSolution(@PathVariable Long id) {
        log.info("========== ДЕМОНСТРАЦИЯ РЕШЕНИЯ ПРОБЛЕМЫ N+1 ==========");
        log.info("2. Запрос С FETCH JOIN (вызов vacancyRepository.findByIdWithJoins()):");
        log.info("   🔍 В репозитории используется @Query с LEFT JOIN FETCH для createdBy и его company");

        Vacancy goodVacancy = vacancyRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Vacancy not found"));

        log.info("   ✅ Загружена вакансия: {}", goodVacancy.getTitle());

        String goodCompanyName = goodVacancy.getCreatedBy().getCompany().getName();
        log.info("   ✅ Компания загружена через FETCH JOIN (без дополнительного запроса): {}", goodCompanyName);

        if (goodVacancy.getCreatedBy() != null) {
            String goodCreatorName = goodVacancy.getCreatedBy().getFirstName();
            log.info("   ✅ Создатель загружен через FETCH JOIN (без дополнительного запроса): {}", goodCreatorName);
        }

        log.info("   📊 ИТОГО: 1 запрос к БД (все данные получены за один раз)");
        log.info("========== Демонстрация завершена ==========");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Демонстрация решения проблемы N+1 выполнена. " +
                "Проверьте логи приложения и количество SQL-запросов.");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyResponse createVacancy(@Valid @RequestBody VacancyCreateRequest request) {
        return vacancyService.createVacancy(request);
    }

    @PutMapping("/{id}")
    public VacancyResponse updateVacancy(@PathVariable Long id, @Valid @RequestBody VacancyCreateRequest request) {
        return vacancyService.updateVacancy(id, request);
    }

    @PatchMapping("/{id}/close")
    public void closeVacancy(@PathVariable Long id) {
        vacancyService.closeVacancy(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
    }
}