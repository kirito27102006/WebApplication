package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.enums.EmployerStatus;
import org.example.jobsearchplatform.model.enums.VacancyStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.VacancyMapper;
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
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private ApplicationRepository applicationRepository;

    private VacancyService vacancyService;

    @BeforeEach
    void setUp() {
        vacancyService = new VacancyService(
                vacancyRepository,
                employerRepository,
                applicationRepository,
                new VacancyMapper()
        );
    }

    @Test
    void createVacancy_success() {
        VacancyCreateRequest request = new VacancyCreateRequest();
        request.setTitle("Java Dev");
        request.setSalary(3000);
        request.setCreatedById(3L);

        Company company = new Company();
        company.setId(9L);
        company.setName("Tech Corp");

        Employer employer = new Employer();
        employer.setId(3L);
        employer.setFirstName("Anna");
        employer.setLastName("Smith");
        employer.setStatus(EmployerStatus.ACTIVE);
        employer.setCompany(company);

        Vacancy saved = new Vacancy();
        saved.setId(100L);
        saved.setTitle("Java Dev");
        saved.setSalary(3000);
        saved.setStatus(VacancyStatus.ACTIVE);
        saved.setCreatedBy(employer);

        when(employerRepository.findById(3L)).thenReturn(Optional.of(employer));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(saved);

        VacancyResponse response = vacancyService.createVacancy(request);

        assertEquals(100L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(3L, response.getCreatedById());
    }

    @Test
    void closeVacancy_changesStatusToClosed() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(11L);
        vacancy.setStatus(VacancyStatus.ACTIVE);

        when(vacancyRepository.findById(11L)).thenReturn(Optional.of(vacancy));

        vacancyService.closeVacancy(11L);

        assertEquals(VacancyStatus.CLOSED, vacancy.getStatus());
    }

    @Test
    void deleteVacancy_withApplications_throws() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(7L);

        when(vacancyRepository.findById(7L)).thenReturn(Optional.of(vacancy));
        when(applicationRepository.existsByVacancyId(7L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> vacancyService.deleteVacancy(7L)
        );

        assertEquals("Cannot delete vacancy with existing applications", ex.getMessage());
        verify(vacancyRepository, never()).delete(any());
    }

    @Test
    void findById_notFound_throws() {
        when(vacancyRepository.findByIdWithJoins(123L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> vacancyService.findById(123L)
        );

        assertEquals("Vacancy not found with id: 123", ex.getMessage());
    }
}
