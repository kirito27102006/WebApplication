package org.example.jobsearchplatform.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.service.VacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping
    public List<VacancyResponse> getAllVacancies(
            @RequestParam(required = false) String status) {
        return vacancyService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponse> getVacancyById(@PathVariable Long id) {
        try {
            VacancyResponse response = vacancyService.findById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyResponse createVacancy(@RequestBody VacancyCreateRequest request) {
        return vacancyService.createVacancy(request);
    }
}
