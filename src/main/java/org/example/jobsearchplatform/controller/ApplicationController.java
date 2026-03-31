package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ApplicationCreateRequest;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.example.jobsearchplatform.service.ApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Tag(name = "Applications", description = "Operations with job applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create application")
    public ApplicationResponse createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        return applicationService.createApplication(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by id")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable @Positive Long id) {
        ApplicationResponse response = applicationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all applications (paged)")
    public ResponseEntity<Page<ApplicationResponse>> getAllApplications(Pageable pageable) {
        return ResponseEntity.ok(applicationService.findAll(pageable));
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Search applications by JPQL")
    public ResponseEntity<List<ApplicationResponse>> searchApplicationsJpql(
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false) @Size(max = 50) String status,
            @RequestParam(required = false) @Size(max = 100) String vacancyTitle,
            @RequestParam(required = false) @Size(max = 100) String resumeTitle) {
        return ResponseEntity.ok(
                applicationService.searchByFiltersJpql(userId, status, vacancyTitle, resumeTitle)
        );
    }

    @GetMapping("/search/native")
    @Operation(summary = "Search applications by native SQL")
    public ResponseEntity<List<ApplicationResponse>> searchApplicationsNative(
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false) @Size(max = 50) String status,
            @RequestParam(required = false) @Size(max = 100) String vacancyTitle,
            @RequestParam(required = false) @Size(max = 100) String resumeTitle) {
        return ResponseEntity.ok(
                applicationService.searchByFiltersNative(userId, status, vacancyTitle, resumeTitle)
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get applications by user id")
    public List<ApplicationResponse> getApplicationsByUser(@PathVariable @Positive Long userId) {
        return applicationService.findByUser(userId);
    }

    @GetMapping("/vacancy/{vacancyId}")
    @Operation(summary = "Get applications by vacancy id")
    public List<ApplicationResponse> getApplicationsByVacancy(@PathVariable @Positive Long vacancyId) {
        return applicationService.findByVacancy(vacancyId);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update application status")
    public ApplicationResponse updateApplicationStatus(
            @PathVariable @Positive Long id,
            @RequestParam @NotBlank @Size(max = 50) String status) {
        return applicationService.updateStatus(id, status);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel application")
    public void cancelApplication(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Long userId) {
        applicationService.cancelApplication(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete application")
    public void deleteApplication(@PathVariable @Positive Long id) {
        applicationService.deleteApplication(id);
    }
}
