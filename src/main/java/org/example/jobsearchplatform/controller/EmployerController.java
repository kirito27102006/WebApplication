package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.EmployerCreateRequest;
import org.example.jobsearchplatform.dto.EmployerResponse;
import org.example.jobsearchplatform.service.EmployerService;
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
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployerResponse> getEmployerById(@PathVariable Long id) {
        EmployerResponse response = employerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    public ResponseEntity<EmployerResponse> getEmployerByEmail(@RequestParam String email) {
        EmployerResponse response = employerService.findByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    public List<EmployerResponse> getEmployersByCompany(@PathVariable Long companyId) {
        return employerService.findByCompany(companyId);
    }

    @GetMapping
    public List<EmployerResponse> getAllEmployers() {
        return employerService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployerResponse createEmployer(@Valid @RequestBody EmployerCreateRequest request) {
        return employerService.createEmployer(request);
    }

    @PutMapping("/{id}")
    public EmployerResponse updateEmployer(@PathVariable Long id, @Valid @RequestBody EmployerCreateRequest request) {
        return employerService.updateEmployer(id, request);
    }

    @PatchMapping("/{id}/block")
    public void blockEmployer(@PathVariable Long id) {
        employerService.blockEmployer(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployer(@PathVariable Long id) {
        employerService.deleteEmployer(id);
    }
}