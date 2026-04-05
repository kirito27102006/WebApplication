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
}
