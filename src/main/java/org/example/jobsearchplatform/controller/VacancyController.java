package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.service.VacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

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
        Map<String, String> result = vacancyService.demonstrateNPlusOneProblem(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/demo/solution/{id}")
    public ResponseEntity<Map<String, String>> demonstrateNPlusOneSolution(@PathVariable Long id) {
        Map<String, String> result = vacancyService.demonstrateNPlusOneSolution(id);
        return ResponseEntity.ok(result);
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