package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.service.VacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vacancies", description = "Operations with vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping("/{id}")
    @Operation(summary = "Get vacancy by id")
    public ResponseEntity<VacancyResponse> getVacancyById(@PathVariable @Positive Long id) {
        VacancyResponse response = vacancyService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all vacancies or search by filters")
    public ResponseEntity<List<VacancyResponse>> getAllVacancies(
            @RequestParam(required = false) @Size(max = 100) String title,
            @RequestParam(required = false) @Size(max = 100) String location,
            @RequestParam(required = false) @PositiveOrZero Integer minSalary,
            @RequestParam(required = false) @PositiveOrZero Integer maxExperience,
            @RequestParam(required = false) @Positive Long companyId) {

        if (companyId != null) {
            return ResponseEntity.ok(vacancyService.findByCompany(companyId));
        } else if (title != null || location != null || minSalary != null || maxExperience != null) {
            return ResponseEntity.ok(vacancyService.searchVacancies(title, location, minSalary, maxExperience));
        }
        return ResponseEntity.ok(vacancyService.findAll());
    }

    @GetMapping("/demo/nplus1/{id}")
    @Operation(summary = "Demonstrate N+1 problem")
    public ResponseEntity<Map<String, String>> demonstrateNPlusOneProblem(@PathVariable @Positive Long id) {
        Map<String, String> result = vacancyService.demonstrateNPlusOneProblem(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/demo/solution/{id}")
    @Operation(summary = "Demonstrate N+1 solution")
    public ResponseEntity<Map<String, String>> demonstrateNPlusOneSolution(@PathVariable @Positive Long id) {
        Map<String, String> result = vacancyService.demonstrateNPlusOneSolution(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create vacancy")
    public VacancyResponse createVacancy(@Valid @RequestBody VacancyCreateRequest request) {
        return vacancyService.createVacancy(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vacancy")
    public VacancyResponse updateVacancy(
            @PathVariable @Positive Long id,
            @Valid @RequestBody VacancyCreateRequest request) {
        return vacancyService.updateVacancy(id, request);
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close vacancy")
    public void closeVacancy(@PathVariable @Positive Long id) {
        vacancyService.closeVacancy(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete vacancy")
    public void deleteVacancy(@PathVariable @Positive Long id) {
        vacancyService.deleteVacancy(id);
    }
}
