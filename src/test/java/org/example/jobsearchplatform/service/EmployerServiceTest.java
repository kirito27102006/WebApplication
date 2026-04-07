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

import java.util.List;
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

    @Test
    void findById_notFound_throws() {
        when(employerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> employerService.findById(1L)
        );

        assertEquals("Employer not found with id: 1", ex.getMessage());
    }

    @Test
    void findById_success() {
        Company company = new Company();
        company.setId(10L);
        company.setName("Tech");

        Employer employer = new Employer();
        employer.setId(1L);
        employer.setStatus(EmployerStatus.ACTIVE);
        employer.setFirstName("A");
        employer.setLastName("B");
        employer.setEmail("a@b.com");
        employer.setCompany(company);
        employer.setCreatedVacancies(new java.util.ArrayList<>());
        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        EmployerResponse response = employerService.findById(1L);

        assertEquals(1L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void findAll_mapsList() {
        Employer employer = new Employer();
        employer.setId(3L);
        employer.setFirstName("Anna");
        employer.setStatus(EmployerStatus.ACTIVE);
        employer.setCompany(new Company());
        when(employerRepository.findAll()).thenReturn(List.of(employer));

        List<EmployerResponse> actual = employerService.findAll();

        assertEquals(1, actual.size());
        assertEquals(3L, actual.get(0).getId());
    }

    @Test
    void findByCompany_mapsList() {
        Employer employer = new Employer();
        employer.setId(4L);
        employer.setFirstName("Kate");
        employer.setStatus(EmployerStatus.ACTIVE);
        employer.setCompany(new Company());
        when(employerRepository.findByCompanyId(10L)).thenReturn(List.of(employer));

        List<EmployerResponse> actual = employerService.findByCompany(10L);

        assertEquals(1, actual.size());
        assertEquals(4L, actual.get(0).getId());
    }

    @Test
    void updateEmployer_updatesAndSaves() {
        Employer existing = new Employer();
        existing.setId(9L);
        existing.setStatus(EmployerStatus.ACTIVE);
        existing.setCreatedVacancies(new java.util.ArrayList<>());
        Company oldCompany = new Company();
        oldCompany.setId(1L);
        oldCompany.setName("Old");
        existing.setCompany(oldCompany);

        Company newCompany = new Company();
        newCompany.setId(2L);
        newCompany.setName("NewCo");

        EmployerCreateRequest request = new EmployerCreateRequest();
        request.setFirstName("New");
        request.setLastName("Name");
        request.setPhoneNumber("+123");
        request.setCompanyId(2L);

        when(employerRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(companyRepository.findById(2L)).thenReturn(Optional.of(newCompany));
        when(employerRepository.save(existing)).thenReturn(existing);

        EmployerResponse response = employerService.updateEmployer(9L, request);

        assertEquals("New", existing.getFirstName());
        assertEquals(2L, existing.getCompany().getId());
        assertEquals(9L, response.getId());
    }

    @Test
    void blockEmployer_setsBlockedStatus() {
        Employer employer = new Employer();
        employer.setId(5L);
        employer.setStatus(EmployerStatus.ACTIVE);
        when(employerRepository.findById(5L)).thenReturn(Optional.of(employer));

        employerService.blockEmployer(5L);

        assertEquals(EmployerStatus.BLOCKED, employer.getStatus());
    }

    @Test
    void deleteEmployer_withoutVacancies_deletes() {
        Employer employer = new Employer();
        employer.setId(12L);
        when(employerRepository.findById(12L)).thenReturn(Optional.of(employer));
        when(vacancyRepository.existsByCreatedById(12L)).thenReturn(false);

        employerService.deleteEmployer(12L);

        verify(employerRepository).delete(employer);
    }

    @Test
    void findByEmail_success() {
        Company company = new Company();
        company.setId(10L);
        company.setName("Tech");

        Employer employer = new Employer();
        employer.setId(1L);
        employer.setEmail("ok@example.com");
        employer.setStatus(EmployerStatus.ACTIVE);
        employer.setCompany(company);
        employer.setCreatedVacancies(new java.util.ArrayList<>());

        when(employerRepository.findByEmail("ok@example.com")).thenReturn(Optional.of(employer));

        EmployerResponse response = employerService.findByEmail("ok@example.com");

        assertEquals(1L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
    }
}
