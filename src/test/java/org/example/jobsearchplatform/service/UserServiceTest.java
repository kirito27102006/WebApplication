package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.jobsearchplatform.dto.UserCreateRequest;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.Skill;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.enums.ResumeStatus;
import org.example.jobsearchplatform.model.enums.UserStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private ApplicationRepository applicationRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                resumeRepository,
                applicationRepository,
                new UserMapper()
        );
    }

    @Test
    void createUser_existingEmail_throws() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("john@example.com");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertEquals("User with email john@example.com already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_whenApplicationsExist_softDeletesUserAndResumes() {
        User user = new User();
        user.setId(5L);
        user.setStatus(UserStatus.ACTIVE);

        Resume resume = new Resume();
        resume.setId(10L);
        resume.setStatus(ResumeStatus.ACTIVE);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(resumeRepository.findByUserId(5L)).thenReturn(List.of(resume));
        when(applicationRepository.existsByResumeId(10L)).thenReturn(true);

        userService.deleteUser(5L);

        assertEquals(UserStatus.DELETED, user.getStatus());
        assertEquals(ResumeStatus.USER_DELETED, resume.getStatus());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_whenNoApplications_hardDeletes() {
        User user = new User();
        user.setId(7L);
        user.setSkills(new ArrayList<>(List.of(new Skill())));

        Resume resume = new Resume();
        resume.setId(11L);

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(resumeRepository.findByUserId(7L)).thenReturn(List.of(resume));
        when(applicationRepository.existsByResumeId(11L)).thenReturn(false);

        userService.deleteUser(7L);

        assertEquals(0, user.getSkills().size());
        verify(resumeRepository).deleteAll(List.of(resume));
        verify(userRepository).delete(user);
    }

    @Test
    void findByEmail_notFound_throws() {
        when(userRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> userService.findByEmail("none@example.com")
        );

        assertEquals("User not found with email: none@example.com", ex.getMessage());
    }
}
