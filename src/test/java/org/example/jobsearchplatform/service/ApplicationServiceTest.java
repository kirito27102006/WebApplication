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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.createApplicationsBulk(List.of(first, second))
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
                () -> applicationService.createApplicationsBulkWithoutTransaction(List.of(first, second))
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
