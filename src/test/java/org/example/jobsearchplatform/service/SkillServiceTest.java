package org.example.jobsearchplatform.service;

import org.example.jobsearchplatform.model.Skill;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.repository.SkillRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;

    private SkillService skillService;

    @BeforeEach
    void setUp() {
        skillService = new SkillService(skillRepository, userRepository);
    }

    @Test
    void createSkills_whenRepositoryEmpty_savesAllDefaults() {
        when(skillRepository.count()).thenReturn(0L);

        skillService.createSkills();

        verify(skillRepository, times(7)).save(any(Skill.class));
    }

    @Test
    void assignSkillsToUsers_whenNoUsers_doesNothing() {
        when(userRepository.count()).thenReturn(0L);

        skillService.assignSkillsToUsers("ivan@example.com");

        verify(skillRepository, never()).findAll();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void assignSkillsToUsers_assignsOnlyMissingSkills() {
        User ivan = new User();
        ivan.setEmail("ivan@example.com");
        ivan.setSkills(new java.util.ArrayList<>());

        Skill java = new Skill();
        java.setName("Java");
        Skill spring = new Skill();
        spring.setName("Spring");
        Skill docker = new Skill();
        docker.setName("Docker");

        when(userRepository.count()).thenReturn(2L);
        when(skillRepository.findAll()).thenReturn(List.of(java, spring, docker));
        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(ivan));
        when(userRepository.findByEmail("maria@example.com")).thenReturn(Optional.empty());

        skillService.assignSkillsToUsers("ivan@example.com");

        assertEquals(2, ivan.getSkills().size());
        verify(userRepository, times(1)).save(ivan);
    }
}
