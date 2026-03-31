package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ResumeCreateRequest;
import org.example.jobsearchplatform.dto.ResumeResponse;
import org.example.jobsearchplatform.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Resumes", description = "Operations with resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/{id}")
    @Operation(summary = "Get resume by id")
    public ResponseEntity<ResumeResponse> getResumeById(@PathVariable @Positive Long id) {
        ResumeResponse response = resumeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all resumes or search by filters")
    public ResponseEntity<List<ResumeResponse>> getAllResumes(
            @RequestParam(required = false) @Size(max = 100) String skill,
            @RequestParam(required = false) @Size(max = 50) String location,
            @RequestParam(required = false) @PositiveOrZero Integer maxSalary) {

        if (skill != null || location != null || maxSalary != null) {
            return ResponseEntity.ok(resumeService.searchResumes(skill, location, maxSalary));
        }
        return ResponseEntity.ok(resumeService.findAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get resumes by user id")
    public List<ResumeResponse> getResumesByUser(@PathVariable @Positive Long userId) {
        return resumeService.findByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create resume")
    public ResumeResponse createResume(@Valid @RequestBody ResumeCreateRequest request) {
        return resumeService.createResume(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update resume")
    public ResumeResponse updateResume(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ResumeCreateRequest request) {
        return resumeService.updateResume(id, request);
    }

    @PatchMapping("/{id}/hide")
    @Operation(summary = "Hide resume")
    public void hideResume(@PathVariable @Positive Long id) {
        resumeService.hideResume(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete resume")
    public void deleteResume(@PathVariable @Positive Long id) {
        resumeService.deleteResume(id);
    }
}
