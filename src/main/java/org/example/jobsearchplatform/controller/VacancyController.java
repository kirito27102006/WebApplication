package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final VacancyRepository vacancyRepository;
    private static final String BP = "</b></p>";
    private static final String DIV = "</div>";

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

    // ВРЕМЕННЫЙ МЕТОД ДЛЯ ДЕМОНСТРАЦИИ N+1 ПРОБЛЕМЫ - ИСПРАВЛЕНО!
    @GetMapping("/bad/{id}")
    public String demonstrateNPlusOne(@PathVariable Long id) {
        StringBuilder log = new StringBuilder("<html><body style='font-family: Arial; padding: 20px;'>");
        log.append("<h2 style='color: #d9534f;'>🔴 ДЕМОНСТРАЦИЯ N+1 ПРОБЛЕМЫ</h2>");

        try {
            // 1. Плохой способ - без fetch join
            log.append("<div style='background-color: #f2dede; padding: 15px; border-radius: 5px; margin: 10px 0;'>");
            log.append("<h3 style='color: #a94442;'>1. Запрос БЕЗ fetch join (будет N+1 запросов):</h3>");

            Vacancy badVacancy = vacancyRepository.findById(id).orElseThrow();

            log.append("<p>✅ Загружена вакансия: <b>").append(badVacancy.getTitle()).append(BP);
            log.append("<p>🔄 Отдельный запрос к компании: <b>").append(badVacancy.getCompany()
                    .getName()).append(BP);

            if (badVacancy.getCreatedBy() != null) {
                log.append("<p>🔄 Отдельный запрос к создателю: <b>").append(badVacancy
                        .getCreatedBy().getFirstName()).append(BP);
            }

            // УДАЛЕНО: обращение к getApplications()

            log.append("<p style='color: #a94442;'><b>📊 ИТОГО: 1 + N запросов к БД</b></p>");
            log.append(DIV);

            // 2. Хороший способ - с fetch join
            log.append("<div style='background-color: #dff0d8; padding: 15px; border-radius: 5px; margin: 10px 0;'>");
            log.append("<h3 style='color: #3c763d;'>2. Запрос С fetch join (один запрос):</h3>");

            Vacancy goodVacancy = vacancyRepository.findByIdWithJoins(id).orElseThrow();

            log.append("<p>✅ Загружена вакансия: <b>").append(goodVacancy.getTitle()).append(BP);
            log.append("<p>✅ Компания (уже загружена): <b>").append(goodVacancy.getCompany()
                    .getName()).append(BP);

            if (goodVacancy.getCreatedBy() != null) {
                log.append("<p>✅ Создатель (уже загружен): <b>").append(goodVacancy
                        .getCreatedBy().getFirstName()).append(BP);
            }

            // УДАЛЕНО: обращение к getApplications()

            log.append("<p style='color: #3c763d;'><b>📊 ИТОГО: 1 запрос к БД</b></p>");
            log.append(DIV);

            log.append("<p style='font-size: 18px;'><b>👉 Смотри в консоль! Во втором случае будет " +
                    "ОДИН запрос вместо N+1</b></p>");

        } catch (Exception e) {
            log.append("<div style='background-color: #f2dede; padding: 15px; border-radius: 5px;'>");
            log.append("<p style='color: #a94442;'><b>❌ Ошибка: ").append(e.getMessage()).append(BP);
            log.append(DIV);
        }

        log.append("</body></html>");
        return log.toString();
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