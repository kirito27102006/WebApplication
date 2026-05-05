package org.example.jobsearchplatform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.EmployerCreateRequest;
import org.example.jobsearchplatform.dto.EmployerResponse;
import org.example.jobsearchplatform.service.AuthService;
import org.example.jobsearchplatform.service.EmployerService;
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
@RequestMapping("/api/employers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Employers", description = "Operations with employers")
public class EmployerController {

    private final EmployerService employerService;

    @GetMapping("/{id}")
    @Operation(summary = "Get employer by id")
    public ResponseEntity<EmployerResponse> getEmployerById(@PathVariable @Positive Long id) {
        EmployerResponse response = employerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get employer by email")
    public ResponseEntity<EmployerResponse> getEmployerByEmail(
            @RequestParam @NotBlank @Email String email) {
        EmployerResponse response = employerService.findByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get employers by company id")
    public List<EmployerResponse> getEmployersByCompany(@PathVariable @Positive Long companyId) {
        return employerService.findByCompany(companyId);
    }

    @GetMapping
    @Operation(summary = "Get all employers")
    public List<EmployerResponse> getAllEmployers() {
        return employerService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create employer")
    public EmployerResponse createEmployer(@Valid @RequestBody EmployerCreateRequest request) {
        return employerService.createEmployer(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employer")
    public EmployerResponse updateEmployer(
            @PathVariable @Positive Long id,
            @Valid @RequestBody EmployerCreateRequest request,
            HttpServletRequest servletRequest) {
        AuthService.SessionPrincipal principal = (AuthService.SessionPrincipal) servletRequest.getAttribute("authPrincipal");
        return employerService.updateOwnedEmployer(id, principal.getCompanyId(), request);
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Block employer")
    public void blockEmployer(@PathVariable @Positive Long id, HttpServletRequest servletRequest) {
        AuthService.SessionPrincipal principal = (AuthService.SessionPrincipal) servletRequest.getAttribute("authPrincipal");
        employerService.blockOwnedEmployer(id, principal.getCompanyId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete employer")
    public void deleteEmployer(@PathVariable @Positive Long id, HttpServletRequest servletRequest) {
        AuthService.SessionPrincipal principal = (AuthService.SessionPrincipal) servletRequest.getAttribute("authPrincipal");
        employerService.deleteOwnedEmployer(id, principal.getCompanyId());
    }
}
