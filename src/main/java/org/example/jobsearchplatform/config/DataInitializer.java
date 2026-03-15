package org.example.jobsearchplatform.config;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.Skill;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.SkillRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Создаем компании
        String name = "TechCorp";
        String city = "Москва";
        if (companyRepository.count() == 0) {
            Company techCorp = new Company();
            techCorp.setName(name);
            techCorp.setDescription("Leading technology company");
            techCorp.setIndustry("IT");
            techCorp.setLocation(city);
            techCorp.setWebsite("https://techcorp.com");
            techCorp.setContactEmail("hr@techcorp.com");
            companyRepository.save(techCorp);

            Company bank = new Company();
            bank.setName("BigBank");
            bank.setDescription("Financial services");
            bank.setIndustry("Finance");
            bank.setLocation("Санкт-Петербург");
            bank.setWebsite("https://bigbank.ru");
            bank.setContactEmail("hr@bigbank.ru");
            companyRepository.save(bank);
        }

        // Создаем работодателей
        if (employerRepository.count() == 0) {
            Company techCorp = companyRepository.findByName(name).orElse(null);
            if (techCorp != null) {
                Employer employer1 = new Employer();
                employer1.setFirstName("Алексей");
                employer1.setLastName("HR-менеджер");
                employer1.setEmail("hr@techcorp.com");
                employer1.setCompany(techCorp);
                employerRepository.save(employer1);
            }
        }

        // Создаем пользователей
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setFirstName("Иван");
            user1.setLastName("Петров");
            user1.setEmail("ivan@example.com");
            user1.setPhoneNumber("+79001234567");
            userRepository.save(user1);

            User user2 = new User();
            user2.setFirstName("Мария");
            user2.setLastName("Сидорова");
            user2.setEmail("maria@example.com");
            user2.setPhoneNumber("+79007654321");
            userRepository.save(user2);
        }

        createSkills();
        assignSkillsToUsers();

        // Создаем вакансии
        if (vacancyRepository.count() == 0) {
            Company techCorp = companyRepository.findByName(name).orElse(null);
            Employer hr = employerRepository.findByEmail("hr@techcorp.com").orElse(null);

            if (techCorp != null) {
                Vacancy vac1 = new Vacancy();
                vac1.setTitle("Java Developer");
                vac1.setDescription("Разработка на Java Spring");
                vac1.setSalary(200000);
                vac1.setRequiredExperience(3);
                vac1.setLocation(city);
                vac1.setCompany(techCorp);
                vac1.setCreatedBy(hr);
                vacancyRepository.save(vac1);

                Vacancy vac2 = new Vacancy();
                vac2.setTitle("Frontend Developer");
                vac2.setDescription("Разработка на React");
                vac2.setSalary(180000);
                vac2.setRequiredExperience(2);
                vac2.setLocation(city);
                vac2.setCompany(techCorp);
                vac2.setCreatedBy(hr);
                vacancyRepository.save(vac2);
            }
        }

        // Создаем резюме
        if (resumeRepository.count() == 0) {
            User ivan = userRepository.findByEmail("ivan@example.com").orElse(null);
            if (ivan != null) {
                Resume resume = new Resume();
                resume.setTitle("Java Developer");
                resume.setSkills("Java, Spring, PostgreSQL");
                resume.setExperience("3 года в разработке");
                resume.setEducation("МГУ, Прикладная математика");
                resume.setExpectedSalary(200000);
                resume.setLocation(city);
                resume.setUser(ivan);
                resumeRepository.save(resume);
            }
        }
    }

    private void createSkills() {
        if (skillRepository.count() == 0) {
            String[] skillNames = {"Java", "Spring", "Hibernate", "PostgreSQL", "React", "Docker", "Git"};
            for (String name : skillNames) {
                Skill skill = new Skill();
                skill.setName(name);
                skillRepository.save(skill);
            }
            System.out.println("=== Созданы навыки ===");
        }
    }

    @Transactional
    public void assignSkillsToUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            // ИСПРАВЛЕНИЕ: Вместо проверки через users.get(0).getSkills().isEmpty()
            // загружаем пользователей с их навыками
            User ivan = userRepository.findByEmail("ivan@example.com").orElse(null);
            User maria = userRepository.findByEmail("maria@example.com").orElse(null);

            if (ivan != null && ivan.getSkills().isEmpty()) {
                List<Skill> javaSkills = skillRepository.findAll().stream()
                        .filter(s -> s.getName().equals("Java") || s.getName().equals("Spring") ||
                                s.getName().equals("Hibernate") || s.getName().equals("PostgreSQL"))
                        .toList();

                ivan.getSkills().addAll(javaSkills);
                userRepository.save(ivan);
                System.out.println("=== Навыки назначены Ивану ===");
            }

            if (maria != null && maria.getSkills().isEmpty()) {
                List<Skill> frontendSkills = skillRepository.findAll().stream()
                        .filter(s -> s.getName().equals("React") || s.getName().equals("Git") ||
                                s.getName().equals("Docker"))
                        .toList();

                maria.getSkills().addAll(frontendSkills);
                userRepository.save(maria);
                System.out.println("=== Навыки назначены Марии ===");
            }
        }
    }
}