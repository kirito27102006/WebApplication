package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.ApplicationCreateRequest;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.example.jobsearchplatform.model.Application;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.enums.ApplicationStatus;
import org.example.jobsearchplatform.model.enums.VacancyStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.ApplicationNativeSearchView;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.ApplicationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private UserRepository userRepository;

    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ApplicationService(
                applicationRepository,
                vacancyRepository,
                resumeRepository,
                userRepository,
                new ApplicationMapper()
        );
    }

    @Test
    void createApplication_success() {
        ApplicationCreateRequest request = buildRequest(10L, 20L, 30L, "Hello");
        Vacancy vacancy = buildVacancy(20L, VacancyStatus.ACTIVE);
        Resume resume = buildResumeWithUser(30L, 10L);

        Application saved = new Application();
        saved.setId(99L);
        saved.setStatus(ApplicationStatus.PENDING);
        saved.setCoverLetter("Hello");
        saved.setVacancy(vacancy);
        saved.setResume(resume);
        when(vacancyRepository.findById(20L)).thenReturn(Optional.of(vacancy));
        when(resumeRepository.findByIdWithUser(30L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByUserIdAndVacancyId(10L, 20L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(saved);

        ApplicationResponse actual = applicationService.createApplication(request);

        assertEquals(99L, actual.getId());
        assertEquals("PENDING", actual.getStatus());
        assertEquals("Hello", actual.getCoverLetter());
        assertEquals(10L, actual.getUserId());
        assertEquals(20L, actual.getVacancyId());
        assertEquals(30L, actual.getResumeId());
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(captor.capture());
        Application toSave = captor.getValue();
        assertEquals(ApplicationStatus.PENDING, toSave.getStatus());
        assertEquals("Hello", toSave.getCoverLetter());
        assertSame(vacancy, toSave.getVacancy());
        assertSame(resume, toSave.getResume());
    }

    @Test
    void createApplicationsBulk_duplicateRequest_throws() {
        ApplicationCreateRequest first = buildRequest(1L, 2L, 3L, "A");
        ApplicationCreateRequest second = buildRequest(1L, 2L, 4L, "B");
        List<ApplicationCreateRequest> requests = List.of(first, second);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.createApplicationsBulk(requests)
        );

        assertEquals(
                "Bulk request contains duplicate application for user 1 and vacancy 2",
                ex.getMessage()
        );
        verify(applicationRepository, never()).saveAll(any());
    }

    @Test
    void createApplicationsBulkWithoutTransaction_secondFails_firstWasPersisted() {
        ApplicationCreateRequest first = buildRequest(10L, 20L, 30L, "ok");
        ApplicationCreateRequest second = buildRequest(10L, 999L, 30L, "bad");
        List<ApplicationCreateRequest> requests = List.of(first, second);

        Vacancy validVacancy = buildVacancy(20L, VacancyStatus.ACTIVE);
        Resume resume = buildResumeWithUser(30L, 10L);
        Application savedFirst = new Application();
        savedFirst.setId(500L);

        when(vacancyRepository.findById(20L)).thenReturn(Optional.of(validVacancy));
        when(vacancyRepository.findById(999L)).thenReturn(Optional.empty());
        when(resumeRepository.findByIdWithUser(30L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByUserIdAndVacancyId(10L, 20L)).thenReturn(false);
        when(applicationRepository.saveAndFlush(any(Application.class))).thenReturn(savedFirst);

        assertThrows(
                EntityNotFoundException.class,
                () -> applicationService.createApplicationsBulkWithoutTransaction(requests)
        );

        verify(applicationRepository).saveAndFlush(any(Application.class));
    }

    @Test
    void findByUser_userMissing_throws() {
        when(userRepository.existsById(777L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> applicationService.findByUser(777L)
        );

        assertEquals("User not found with id: 777", ex.getMessage());
        verify(applicationRepository, never()).findByUserIdWithJoins(any());
    }

    @Test
    void updateStatus_invalidStatus_throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.updateStatus(1L, "WRONG_STATUS")
        );
        assertEquals("Invalid status: WRONG_STATUS", ex.getMessage());
        verify(applicationRepository, never()).findById(any());
    }

    @Test
    void findAll_usesCacheOnSecondCall() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Application application = new Application();
        application.setId(1L);
        Page<Application> applications = new PageImpl<>(List.of(application));
        when(applicationRepository.findAllWithJoins(pageable)).thenReturn(applications);

        Page<ApplicationResponse> first = applicationService.findAll(pageable);
        Page<ApplicationResponse> second = applicationService.findAll(pageable);

        assertEquals(1, first.getTotalElements());
        assertEquals(1, second.getTotalElements());
        verify(applicationRepository, times(1)).findAllWithJoins(pageable);
    }

    @Test
    void createApplicationsBulk_success() {
        ApplicationCreateRequest request = buildRequest(10L, 20L, 30L, "Bulk");
        Vacancy vacancy = buildVacancy(20L, VacancyStatus.ACTIVE);
        Resume resume = buildResumeWithUser(30L, 10L);
        Application saved = new Application();
        saved.setId(555L);
        saved.setStatus(ApplicationStatus.PENDING);
        saved.setVacancy(vacancy);
        saved.setResume(resume);
        saved.setCoverLetter("Bulk");

        when(vacancyRepository.findById(20L)).thenReturn(Optional.of(vacancy));
        when(resumeRepository.findByIdWithUser(30L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByUserIdAndVacancyId(10L, 20L)).thenReturn(false);
        when(applicationRepository.saveAll(any())).thenReturn(List.of(saved));

        List<ApplicationResponse> responses = applicationService.createApplicationsBulk(List.of(request));

        assertEquals(1, responses.size());
        assertEquals(555L, responses.get(0).getId());
        verify(applicationRepository).saveAll(any());
    }

    @Test
    void createApplicationsBulk_empty_throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.createApplicationsBulk(List.of())
        );
        assertEquals("Bulk request must contain at least one item", ex.getMessage());
    }

    @Test
    void createApplication_resumeBelongsToAnotherUser_throws() {
        ApplicationCreateRequest request = buildRequest(10L, 20L, 30L, "Hello");
        Vacancy vacancy = buildVacancy(20L, VacancyStatus.ACTIVE);
        Resume resume = buildResumeWithUser(30L, 99L);

        when(vacancyRepository.findById(20L)).thenReturn(Optional.of(vacancy));
        when(resumeRepository.findByIdWithUser(30L)).thenReturn(Optional.of(resume));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.createApplication(request)
        );

        assertEquals("Resume does not belong to the user", ex.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void createApplication_duplicateByUserAndVacancy_throws() {
        ApplicationCreateRequest request = buildRequest(10L, 20L, 30L, "Hello");
        Vacancy vacancy = buildVacancy(20L, VacancyStatus.ACTIVE);
        Resume resume = buildResumeWithUser(30L, 10L);

        when(vacancyRepository.findById(20L)).thenReturn(Optional.of(vacancy));
        when(resumeRepository.findByIdWithUser(30L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByUserIdAndVacancyId(10L, 20L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> applicationService.createApplication(request)
        );

        assertEquals("User has already applied to this vacancy", ex.getMessage());
    }

    @Test
    void findByVacancy_notFound_throws() {
        when(vacancyRepository.existsById(5L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> applicationService.findByVacancy(5L)
        );

        assertEquals("Vacancy not found with id: 5", ex.getMessage());
    }

    @Test
    void searchByFiltersJpql_blankStatus_usesNullStatus() {
        Application application = new Application();
        application.setId(1L);
        application.setStatus(ApplicationStatus.PENDING);
        when(applicationRepository.searchByFiltersJpql(eq(10L), eq(null), eq(""), eq("resume")))
                .thenReturn(List.of(application));

        List<ApplicationResponse> responses = applicationService.searchByFiltersJpql(10L, "  ", null, " resume ");

        assertEquals(1, responses.size());
        verify(applicationRepository).searchByFiltersJpql(10L, null, "", "resume");
    }

    @Test
    void searchByFiltersNative_mapsProjection() {
        ApplicationNativeSearchView view = new ApplicationNativeSearchView() {
            @Override
            public Long getId() { return 7L; }
            @Override
            public Long getUserId() { return 8L; }
            @Override
            public String getUserFullName() { return "Ivan Petrov"; }
            @Override
            public Long getVacancyId() { return 9L; }
            @Override
            public String getVacancyTitle() { return "Backend"; }
            @Override
            public Long getResumeId() { return 10L; }
            @Override
            public String getResumeTitle() { return "Java Resume"; }
            @Override
            public String getCoverLetter() { return "Hi"; }
            @Override
            public String getStatus() { return "PENDING"; }
            @Override
            public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
        };
        when(applicationRepository.searchByFiltersNative(8L, "PENDING", "Back", null))
                .thenReturn(List.of(view));

        List<ApplicationResponse> responses = applicationService.searchByFiltersNative(8L, "pending", " Back ", " ");

        assertEquals(1, responses.size());
        assertEquals(7L, responses.get(0).getId());
        assertEquals("PENDING", responses.get(0).getStatus());
    }

    @Test
    void cancelApplication_whenNotOwner_throws() {
        Application application = new Application();
        Resume resume = buildResumeWithUser(30L, 10L);
        application.setResume(resume);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> applicationService.cancelApplication(1L, 999L)
        );

        assertEquals("You can only cancel your own applications", ex.getMessage());
    }

    @Test
    void cancelApplication_success() {
        Application application = new Application();
        Resume resume = buildResumeWithUser(30L, 10L);
        application.setResume(resume);
        application.setStatus(ApplicationStatus.PENDING);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        applicationService.cancelApplication(1L, 10L);

        assertEquals(ApplicationStatus.CANCELLED, application.getStatus());
    }

    @Test
    void deleteApplication_delegatesToRepository() {
        applicationService.deleteApplication(123L);
        verify(applicationRepository).deleteById(123L);
    }

    private static ApplicationCreateRequest buildRequest(Long userId, Long vacancyId, Long resumeId, String coverLetter) {
        ApplicationCreateRequest request = new ApplicationCreateRequest();
        request.setUserId(userId);
        request.setVacancyId(vacancyId);
        request.setResumeId(resumeId);
        request.setCoverLetter(coverLetter);
        return request;
    }

    private static Vacancy buildVacancy(Long id, VacancyStatus status) {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(id);
        vacancy.setStatus(status);
        vacancy.setTitle("Java Dev");
        return vacancy;
    }

    private static Resume buildResumeWithUser(Long resumeId, Long userId) {
        User user = new User();
        user.setId(userId);
        user.setFirstName("Ivan");
        user.setLastName("Petrov");

        Resume resume = new Resume();
        resume.setId(resumeId);
        resume.setTitle("Backend Resume");
        resume.setUser(user);
        return resume;
    }
}
