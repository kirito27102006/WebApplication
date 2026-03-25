package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ResumeCreateRequest;
import org.example.jobsearchplatform.dto.ResumeResponse;
import org.example.jobsearchplatform.service.ResumeService;
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
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponse> getResumeById(@PathVariable Long id) {
        ResumeResponse response = resumeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getAllResumes(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxSalary) {

        if (skill != null || location != null || maxSalary != null) {
            return ResponseEntity.ok(resumeService.searchResumes(skill, location, maxSalary));
        }
        return ResponseEntity.ok(resumeService.findAll());
    }

    @GetMapping("/user/{userId}")
    public List<ResumeResponse> getResumesByUser(@PathVariable Long userId) {
        return resumeService.findByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponse createResume(@Valid @RequestBody ResumeCreateRequest request) {
        return resumeService.createResume(request);
    }

    @PutMapping("/{id}")
    public ResumeResponse updateResume(@PathVariable Long id, @Valid @RequestBody ResumeCreateRequest request) {
        return resumeService.updateResume(id, request);
    }

    @PatchMapping("/{id}/hide")
    public void hideResume(@PathVariable Long id) {
        resumeService.hideResume(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
    }
}