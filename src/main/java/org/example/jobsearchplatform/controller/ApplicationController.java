package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ApplicationCreateRequest;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.example.jobsearchplatform.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        return applicationService.createApplication(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        ApplicationResponse response = applicationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public List<ApplicationResponse> getApplicationsByUser(@PathVariable Long userId) {
        return applicationService.findByUser(userId);
    }

    @GetMapping("/vacancy/{vacancyId}")
    public List<ApplicationResponse> getApplicationsByVacancy(@PathVariable Long vacancyId) {
        return applicationService.findByVacancy(vacancyId);
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return applicationService.updateStatus(id, status);
    }

    @PatchMapping("/{id}/cancel")
    public void cancelApplication(
            @PathVariable Long id,
            @RequestParam Long userId) {
        applicationService.cancelApplication(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
    }
}