package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.EmployerCreateRequest;
import org.example.jobsearchplatform.dto.EmployerResponse;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.enums.EmployerStatus;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.EmployerMapper;
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
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private VacancyRepository vacancyRepository;

    private EmployerService employerService;

    @BeforeEach
    void setUp() {
        employerService = new EmployerService(
                employerRepository,
                companyRepository,
                vacancyRepository,
                new EmployerMapper()
        );
    }

    @Test
    void createEmployer_existingEmail_throws() {
        EmployerCreateRequest request = new EmployerCreateRequest();
        request.setEmail("hr@example.com");

        when(employerRepository.existsByEmail("hr@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employerService.createEmployer(request)
        );

        assertEquals("Employer with email hr@example.com already exists", ex.getMessage());
        verify(employerRepository, never()).save(any());
    }

    @Test
    void createEmployer_success() {
        EmployerCreateRequest request = new EmployerCreateRequest();
        request.setFirstName("Anna");
        request.setLastName("Smith");
        request.setEmail("anna@corp.com");
        request.setPhoneNumber("+375291112233");
        request.setCompanyId(10L);

        Company company = new Company();
        company.setId(10L);
        company.setName("Tech Corp");

        Employer saved = new Employer();
        saved.setId(20L);
        saved.setFirstName("Anna");
        saved.setLastName("Smith");
        saved.setEmail("anna@corp.com");
        saved.setPhoneNumber("+375291112233");
        saved.setStatus(EmployerStatus.ACTIVE);
        saved.setCompany(company);

        when(employerRepository.existsByEmail("anna@corp.com")).thenReturn(false);
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(employerRepository.save(any(Employer.class))).thenReturn(saved);

        EmployerResponse response = employerService.createEmployer(request);

        assertEquals(20L, response.getId());
        assertEquals("Anna", response.getFirstName());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(10L, response.getCompanyId());
    }

    @Test
    void deleteEmployer_withVacancies_throws() {
        Employer employer = new Employer();
        employer.setId(77L);

        when(employerRepository.findById(77L)).thenReturn(Optional.of(employer));
        when(vacancyRepository.existsByCreatedById(77L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> employerService.deleteEmployer(77L)
        );

        assertEquals("Cannot delete employer with existing vacancies", ex.getMessage());
        verify(employerRepository, never()).delete(any());
    }

    @Test
    void findByEmail_notFound_throws() {
        when(employerRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> employerService.findByEmail("none@example.com")
        );

        assertEquals("Employer not found with email: none@example.com", ex.getMessage());
    }
}
