package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.ResumeCreateRequest;
import org.example.jobsearchplatform.dto.ResumeResponse;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.enums.ResumeStatus;
import org.example.jobsearchplatform.model.enums.UserStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.service.mapper.ResumeMapper;
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
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationRepository applicationRepository;

    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        resumeService = new ResumeService(
                resumeRepository,
                userRepository,
                applicationRepository,
                new ResumeMapper()
        );
    }

    @Test
    void createResume_success() {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setUserId(1L);
        request.setTitle("Java Developer");
        request.setSkills("Java");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Ivan");
        user.setLastName("Petrov");
        user.setStatus(UserStatus.ACTIVE);

        Resume saved = new Resume();
        saved.setId(5L);
        saved.setTitle("Java Developer");
        saved.setSkills("Java");
        saved.setStatus(ResumeStatus.ACTIVE);
        saved.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resumeRepository.save(any(Resume.class))).thenReturn(saved);

        ResumeResponse response = resumeService.createResume(request);

        assertEquals(5L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void findByUser_userMissing_throws() {
        when(userRepository.existsById(77L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> resumeService.findByUser(77L)
        );

        assertEquals("User not found with id: 77", ex.getMessage());
        verify(resumeRepository, never()).findByUserIdWithUser(any());
    }

    @Test
    void deleteResume_withApplications_throws() {
        Resume resume = new Resume();
        resume.setId(10L);

        when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByResumeId(10L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> resumeService.deleteResume(10L)
        );

        assertEquals("Cannot delete resume because it has associated applications", ex.getMessage());
        verify(resumeRepository, never()).delete(any());
    }

    @Test
    void findAll_mapsList() {
        Resume resume = new Resume();
        resume.setId(2L);
        resume.setTitle("Java");
        resume.setStatus(ResumeStatus.ACTIVE);
        User user = new User();
        user.setId(1L);
        user.setFirstName("Ivan");
        user.setLastName("Petrov");
        resume.setUser(user);
        when(resumeRepository.findAllWithUser()).thenReturn(List.of(resume));

        List<ResumeResponse> responses = resumeService.findAll();

        assertEquals(1, responses.size());
        assertEquals(2L, responses.get(0).getId());
    }

    @Test
    void findById_notFound_throws() {
        when(resumeRepository.findByIdWithUser(8L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> resumeService.findById(8L)
        );

        assertEquals("Resume not found with id: 8", ex.getMessage());
    }

    @Test
    void searchResumes_prefersSkillFilter() {
        Resume resume = new Resume();
        resume.setId(3L);
        resume.setStatus(ResumeStatus.ACTIVE);
        User user = new User();
        user.setId(1L);
        user.setFirstName("Ivan");
        user.setLastName("Petrov");
        resume.setUser(user);
        when(resumeRepository.findBySkillWithUser("Java")).thenReturn(List.of(resume));

        List<ResumeResponse> responses = resumeService.searchResumes("Java", "Minsk", 1000);

        assertEquals(1, responses.size());
        verify(resumeRepository).findBySkillWithUser("Java");
        verify(resumeRepository, never()).findByLocationWithUser(any());
    }

    @Test
    void searchResumes_usesLocationWhenSkillBlank() {
        Resume resume = new Resume();
        resume.setId(4L);
        resume.setStatus(ResumeStatus.ACTIVE);
        User user = new User();
        user.setId(2L);
        user.setFirstName("Anna");
        user.setLastName("Smith");
        resume.setUser(user);
        when(resumeRepository.findByLocationWithUser("Minsk")).thenReturn(List.of(resume));

        List<ResumeResponse> responses = resumeService.searchResumes(" ", "Minsk", 2000);

        assertEquals(1, responses.size());
        verify(resumeRepository).findByLocationWithUser("Minsk");
    }

    @Test
    void searchResumes_usesMaxSalaryWhenNoSkillAndLocation() {
        Resume resume = new Resume();
        resume.setId(5L);
        resume.setStatus(ResumeStatus.ACTIVE);
        User user = new User();
        user.setId(3L);
        user.setFirstName("Kate");
        user.setLastName("Jones");
        resume.setUser(user);
        when(resumeRepository.findByMaxSalaryWithUser(3000)).thenReturn(List.of(resume));

        List<ResumeResponse> responses = resumeService.searchResumes(null, " ", 3000);

        assertEquals(1, responses.size());
        verify(resumeRepository).findByMaxSalaryWithUser(3000);
    }

    @Test
    void searchResumes_fallsBackToAll() {
        Resume resume = new Resume();
        resume.setId(6L);
        resume.setStatus(ResumeStatus.ACTIVE);
        User user = new User();
        user.setId(4L);
        user.setFirstName("Max");
        user.setLastName("Payne");
        resume.setUser(user);
        when(resumeRepository.findAllWithUser()).thenReturn(List.of(resume));

        List<ResumeResponse> responses = resumeService.searchResumes(null, null, null);

        assertEquals(1, responses.size());
        verify(resumeRepository).findAllWithUser();
    }

    @Test
    void updateResume_updatesAndSaves() {
        Resume existing = new Resume();
        existing.setId(11L);
        existing.setStatus(ResumeStatus.ACTIVE);
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setTitle("New title");
        request.setSkills("Java,Spring");
        request.setExperience("3 years");
        request.setEducation("BS");
        request.setExpectedSalary(5000);
        request.setLocation("Warsaw");

        User user = new User();
        user.setId(9L);
        user.setFirstName("Nick");
        user.setLastName("White");
        existing.setUser(user);
        when(resumeRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(resumeRepository.save(existing)).thenReturn(existing);

        ResumeResponse response = resumeService.updateResume(11L, request);

        assertEquals("New title", existing.getTitle());
        assertEquals("Warsaw", existing.getLocation());
        assertEquals(11L, response.getId());
    }

    @Test
    void hideResume_setsHiddenStatus() {
        Resume resume = new Resume();
        resume.setId(15L);
        resume.setStatus(ResumeStatus.ACTIVE);
        when(resumeRepository.findById(15L)).thenReturn(Optional.of(resume));

        resumeService.hideResume(15L);

        assertEquals(ResumeStatus.HIDDEN, resume.getStatus());
    }

    @Test
    void deleteResume_withoutApplications_deletes() {
        Resume resume = new Resume();
        resume.setId(20L);
        when(resumeRepository.findById(20L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByResumeId(20L)).thenReturn(false);

        resumeService.deleteResume(20L);

        verify(resumeRepository).delete(resume);
    }

    @Test
    void createResume_userNotFound_throws() {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setUserId(999L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> resumeService.createResume(request)
        );

        assertEquals("User not found with id: 999", ex.getMessage());
    }

    @Test
    void findByUser_success() {
        Resume resume = new Resume();
        resume.setId(50L);
        resume.setStatus(ResumeStatus.ACTIVE);
        User user = new User();
        user.setId(7L);
        user.setFirstName("Ivan");
        user.setLastName("Petrov");
        resume.setUser(user);
        when(userRepository.existsById(7L)).thenReturn(true);
        when(resumeRepository.findByUserIdWithUser(7L)).thenReturn(List.of(resume));

        List<ResumeResponse> responses = resumeService.findByUser(7L);

        assertEquals(1, responses.size());
        assertEquals(50L, responses.get(0).getId());
    }
}
