package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.model.Skill;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.repository.SkillRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createSkills() {
        if (skillRepository.count() == 0) {
            String[] skillNames = {"Java", "Spring", "Hibernate", "PostgreSQL", "React", "Docker", "Git"};
            for (String name : skillNames) {
                Skill skill = new Skill();
                skill.setName(name);
                skillRepository.save(skill);
            }
            log.info("=== Созданы навыки ===");
        }
    }

    @Transactional
    public void assignSkillsToUsers(String emailTwo) {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            User ivan = userRepository.findByEmail(emailTwo).orElse(null);
            User maria = userRepository.findByEmail("maria@example.com").orElse(null);

            if (ivan != null && ivan.getSkills().isEmpty()) {
                List<Skill> javaSkills = skillRepository.findAll().stream()
                        .filter(s -> s.getName().equals("Java") || s.getName().equals("Spring") ||
                                s.getName().equals("Hibernate") || s.getName().equals("PostgreSQL"))
                        .toList();

                ivan.getSkills().addAll(javaSkills);
                userRepository.save(ivan);
                log.info("=== Навыки назначены Ивану ===");
            }

            if (maria != null && maria.getSkills().isEmpty()) {
                List<Skill> frontendSkills = skillRepository.findAll().stream()
                        .filter(s -> s.getName().equals("React") || s.getName().equals("Git") ||
                                s.getName().equals("Docker"))
                        .toList();

                maria.getSkills().addAll(frontendSkills);
                userRepository.save(maria);
                log.info("=== Навыки назначены Марии ===");
            }
        }
    }
}