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

import java.util.List;
import java.util.Map;
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

    @Test
    void findById_success() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(123L);
        vacancy.setTitle("Java Dev");
        vacancy.setStatus(VacancyStatus.ACTIVE);
        Employer employer = new Employer();
        employer.setId(3L);
        employer.setFirstName("Anna");
        employer.setLastName("Smith");
        Company company = new Company();
        company.setId(9L);
        company.setName("Tech Corp");
        employer.setCompany(company);
        vacancy.setCreatedBy(employer);

        when(vacancyRepository.findByIdWithJoins(123L)).thenReturn(Optional.of(vacancy));

        VacancyResponse response = vacancyService.findById(123L);

        assertEquals(123L, response.getId());
        assertEquals("Java Dev", response.getTitle());
    }

    @Test
    void findAll_mapsList() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setTitle("Java");
        Employer employer = new Employer();
        employer.setId(2L);
        employer.setCompany(new Company());
        vacancy.setCreatedBy(employer);
        vacancy.setStatus(VacancyStatus.ACTIVE);
        when(vacancyRepository.findAllWithJoins()).thenReturn(List.of(vacancy));

        List<VacancyResponse> responses = vacancyService.findAll();

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }

    @Test
    void searchVacancies_mapsList() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(10L);
        vacancy.setTitle("Backend");
        Employer employer = new Employer();
        employer.setId(3L);
        employer.setCompany(new Company());
        vacancy.setCreatedBy(employer);
        vacancy.setStatus(VacancyStatus.ACTIVE);
        when(vacancyRepository.searchVacanciesWithJoins("Back", "Minsk", 1000, 2))
                .thenReturn(List.of(vacancy));

        List<VacancyResponse> responses = vacancyService.searchVacancies("Back", "Minsk", 1000, 2);

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    void findByCompany_mapsList() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(11L);
        vacancy.setTitle("QA");
        Employer employer = new Employer();
        employer.setId(3L);
        employer.setCompany(new Company());
        vacancy.setCreatedBy(employer);
        vacancy.setStatus(VacancyStatus.ACTIVE);
        when(vacancyRepository.findByCompanyIdWithJoins(9L)).thenReturn(List.of(vacancy));

        List<VacancyResponse> responses = vacancyService.findByCompany(9L);

        assertEquals(1, responses.size());
        assertEquals(11L, responses.get(0).getId());
    }

    @Test
    void updateVacancy_withNullCreatedBy_keepsNullCreator() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(20L);
        vacancy.setStatus(VacancyStatus.ACTIVE);
        VacancyCreateRequest request = new VacancyCreateRequest();
        request.setTitle("Updated");
        request.setDescription("Desc");
        request.setSalary(2000);
        request.setRequiredExperience(2);
        request.setLocation("Remote");
        request.setCreatedById(null);

        when(vacancyRepository.findById(20L)).thenReturn(Optional.of(vacancy));

        VacancyResponse response = vacancyService.updateVacancy(20L, request);

        assertEquals("Updated", vacancy.getTitle());
        assertEquals(null, vacancy.getCreatedBy());
        assertEquals(20L, response.getId());
    }

    @Test
    void deleteVacancy_withoutApplications_deletes() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(13L);
        when(vacancyRepository.findById(13L)).thenReturn(Optional.of(vacancy));
        when(applicationRepository.existsByVacancyId(13L)).thenReturn(false);

        vacancyService.deleteVacancy(13L);

        verify(vacancyRepository).delete(vacancy);
    }

    @Test
    void demonstrateNPlusOneProblem_returnsFields() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(33L);
        vacancy.setTitle("Title");

        Company company = new Company();
        company.setName("Comp");

        Employer employer = new Employer();
        employer.setFirstName("Ann");
        employer.setCompany(company);
        vacancy.setCreatedBy(employer);

        when(vacancyRepository.findById(33L)).thenReturn(Optional.of(vacancy));

        Map<String, String> result = vacancyService.demonstrateNPlusOneProblem(33L);

        assertEquals("Title", result.get("vacancyTitle"));
        assertEquals("Comp", result.get("companyName"));
        assertEquals("Ann", result.get("creatorName"));
    }

    @Test
    void demonstrateNPlusOneSolution_notFound_throws() {
        when(vacancyRepository.findByIdWithJoins(34L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> vacancyService.demonstrateNPlusOneSolution(34L)
        );

        assertEquals("Vacancy not found", ex.getMessage());
    }

    @Test
    void demonstrateNPlusOneSolution_success() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(35L);
        vacancy.setTitle("Java");
        vacancy.setStatus(VacancyStatus.ACTIVE);

        Company company = new Company();
        company.setName("Tech");

        Employer employer = new Employer();
        employer.setFirstName("John");
        employer.setCompany(company);
        vacancy.setCreatedBy(employer);

        when(vacancyRepository.findByIdWithJoins(35L)).thenReturn(Optional.of(vacancy));

        Map<String, String> result = vacancyService.demonstrateNPlusOneSolution(35L);

        assertEquals("Java", result.get("vacancyTitle"));
        assertEquals("Tech", result.get("companyName"));
        assertEquals("John", result.get("creatorName"));
    }

    @Test
    void updateVacancy_withCreatorId_updatesCreator() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(40L);
        vacancy.setStatus(VacancyStatus.ACTIVE);

        Employer creator = new Employer();
        creator.setId(5L);
        creator.setFirstName("A");
        creator.setLastName("B");
        creator.setCompany(new Company());

        VacancyCreateRequest request = new VacancyCreateRequest();
        request.setCreatedById(5L);
        request.setTitle("Title");

        when(vacancyRepository.findById(40L)).thenReturn(Optional.of(vacancy));
        when(employerRepository.findById(5L)).thenReturn(Optional.of(creator));

        VacancyResponse response = vacancyService.updateVacancy(40L, request);

        assertEquals(5L, vacancy.getCreatedBy().getId());
        assertEquals(40L, response.getId());
    }
}
