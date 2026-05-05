package org.example.jobsearchplatform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.CompanyCreateRequest;
import org.example.jobsearchplatform.dto.CompanyResponse;
import org.example.jobsearchplatform.service.AuthService;
import org.example.jobsearchplatform.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Validated
@Tag(name = "Companies", description = "Operations with companies")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/{id}")
    @Operation(summary = "Get company by id")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable @Positive Long id) {
        CompanyResponse response = companyService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get company by name")
    public ResponseEntity<CompanyResponse> getCompanyByName(
            @RequestParam @NotBlank @Size(max = 100) String name) {
        CompanyResponse response = companyService.findByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search companies by keyword")
    public List<CompanyResponse> searchCompanies(@RequestParam @NotBlank @Size(max = 100) String keyword) {
        return companyService.searchByName(keyword);
    }

    @GetMapping
    @Operation(summary = "Get all companies or filter by industry")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(
            @RequestParam(required = false) @Size(max = 100) String industry) {
        if (industry != null) {
            return ResponseEntity.ok(companyService.findByIndustry(industry));
        }
        return ResponseEntity.ok(companyService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create company")
    public CompanyResponse createCompany(@Valid @RequestBody CompanyCreateRequest request) {
        return companyService.createCompany(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update company")
    public CompanyResponse updateCompany(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CompanyCreateRequest request,
            HttpServletRequest servletRequest) {
        AuthService.SessionPrincipal principal = (AuthService.SessionPrincipal) servletRequest.getAttribute("authPrincipal");
        return companyService.updateOwnedCompany(id, principal.getCompanyId(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete company")
    public void deleteCompany(@PathVariable @Positive Long id, HttpServletRequest servletRequest) {
        AuthService.SessionPrincipal principal = (AuthService.SessionPrincipal) servletRequest.getAttribute("authPrincipal");
        companyService.deleteOwnedCompany(id, principal.getCompanyId());
    }
}
