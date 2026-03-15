package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.CompanyCreateRequest;
import org.example.jobsearchplatform.dto.CompanyResponse;
import org.example.jobsearchplatform.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id) {
        CompanyResponse response = companyService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-name")
    public ResponseEntity<CompanyResponse> getCompanyByName(@RequestParam String name) {
        CompanyResponse response = companyService.findByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public List<CompanyResponse> searchCompanies(@RequestParam String keyword) {
        return companyService.searchByName(keyword);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(
            @RequestParam(required = false) String industry) {
        if (industry != null) {
            return ResponseEntity.ok(companyService.findByIndustry(industry));
        }
        return ResponseEntity.ok(companyService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse createCompany(@Valid @RequestBody CompanyCreateRequest request) {
        return companyService.createCompany(request);
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyCreateRequest request) {
        return companyService.updateCompany(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}