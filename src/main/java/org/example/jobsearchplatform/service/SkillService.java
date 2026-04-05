package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.model.Skill;
import org.example.jobsearchplatform.repository.SkillRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createSkills() {
        if (skillRepository.count() == 0) {
            Arrays.stream(new String[]{"Java", "Spring", "Hibernate", "PostgreSQL", "React", "Docker", "Git"})
                    .map(this::buildSkill)
                    .forEach(skillRepository::save);
            log.info("=== Skills created ===");
        }
    }

    @Transactional
    public void assignSkillsToUsers(String emailTwo) {
        if (userRepository.count() == 0) {
            return;
        }

        List<Skill> skills = skillRepository.findAll();
        assignSkillsIfNeeded(emailTwo, skills, Set.of("Java", "Spring", "Hibernate", "PostgreSQL"),
                "=== Skills assigned to Ivan ===");
        assignSkillsIfNeeded("maria@example.com", skills, Set.of("React", "Git", "Docker"),
                "=== Skills assigned to Maria ===");
    }

    private Skill buildSkill(String name) {
        Skill skill = new Skill();
        skill.setName(name);
        return skill;
    }

    private void assignSkillsIfNeeded(String email, List<Skill> skills, Set<String> skillNames, String logMessage) {
        userRepository.findByEmail(email)
                .filter(user -> user.getSkills().isEmpty())
                .ifPresent(user -> {
                    user.getSkills().addAll(filterSkillsByName(skills, skillNames));
                    userRepository.save(user);
                    log.info(logMessage);
                });
    }

    private List<Skill> filterSkillsByName(List<Skill> skills, Set<String> skillNames) {
        return skills.stream()
                .filter(skill -> Optional.ofNullable(skill.getName())
                        .map(skillNames::contains)
                        .orElse(false))
                .toList();
    }
}
