package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.CompanyCreateRequest;
import org.example.jobsearchplatform.dto.CompanyResponse;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.service.mapper.CompanyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, new CompanyMapper());
    }

    @Test
    void createCompany_whenExists_throws() {
        CompanyCreateRequest request = new CompanyCreateRequest();
        request.setName("Tech Corp");
        when(companyRepository.existsByName("Tech Corp")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> companyService.createCompany(request)
        );

        assertEquals("Company with name Tech Corp already exists", ex.getMessage());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void createCompany_success() {
        CompanyCreateRequest request = new CompanyCreateRequest();
        request.setName("Tech Corp");
        Company saved = new Company();
        saved.setId(42L);
        saved.setName("Tech Corp");

        when(companyRepository.existsByName("Tech Corp")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(saved);

        CompanyResponse actual = companyService.createCompany(request);

        assertEquals(42L, actual.getId());
        assertEquals("Tech Corp", actual.getName());
    }

    @Test
    void findByName_notFound_throws() {
        when(companyRepository.findByName("NoName")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.findByName("NoName")
        );

        assertEquals("Company not found with name: NoName", ex.getMessage());
    }

    @Test
    void updateCompany_updatesFieldsAndSaves() {
        Company existing = new Company();
        existing.setId(1L);
        existing.setName("Old");

        CompanyCreateRequest request = new CompanyCreateRequest();
        request.setName("New");
        request.setDescription("Desc");
        request.setIndustry("IT");
        request.setLocation("Minsk");
        request.setWebsite("https://example.com");
        request.setContactEmail("info@example.com");
        request.setContactPhone("+375291112233");

        Company saved = new Company();
        saved.setId(1L);
        saved.setName("New");
        saved.setEmployers(java.util.List.of());

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(companyRepository.save(existing)).thenReturn(saved);

        CompanyResponse actual = companyService.updateCompany(1L, request);

        assertEquals(1L, actual.getId());
        assertEquals("New", actual.getName());
        assertEquals("New", existing.getName());
        assertEquals("Desc", existing.getDescription());
        assertEquals("IT", existing.getIndustry());
        assertEquals("Minsk", existing.getLocation());
        assertEquals("https://example.com", existing.getWebsite());
        assertEquals("info@example.com", existing.getContactEmail());
        assertEquals("+375291112233", existing.getContactPhone());
    }
}
