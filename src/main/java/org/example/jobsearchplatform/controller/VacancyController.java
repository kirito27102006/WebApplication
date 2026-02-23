package org.example.jobsearchplatform.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.service.VacancyService;
import org.springframework.http.HttpStatus;
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

@RestController  // Объединяет @Controller и @ResponseBody
@RequestMapping("/api/vacancies")  // Базовый URL для всех методов в этом контроллере
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping
    public List<VacancyResponse> getAllVacancies(
            @RequestParam(required = false) String status) {
        return vacancyService.findAll();
    }

    @GetMapping("/{id}")
    public VacancyResponse getVacancyById(@PathVariable Long id) {
        return vacancyService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // Возвращаем статус 201 Created
    public VacancyResponse createVacancy(@RequestBody VacancyCreateRequest request) {
        return vacancyService.createVacancy(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // Возвращаем статус 204 No Content
    public void deleteVacancy(@PathVariable Long id) throws NoSuchFieldException {
        vacancyService.deleteVacancy(id);
    }
}
