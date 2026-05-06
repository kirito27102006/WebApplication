package org.example.jobsearchplatform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.model.Application;
import org.example.jobsearchplatform.model.AuthAccount;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.Skill;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.enums.AccountRole;
import org.example.jobsearchplatform.model.enums.ApplicationStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.AuthAccountRepository;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.SkillRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final VacancyRepository vacancyRepository;
    private final ApplicationRepository applicationRepository;
    private final SkillRepository skillRepository;
    private final AuthAccountRepository authAccountRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.demo.seed-enabled:false}")
    private boolean seedEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Demo data seeding is disabled");
            return;
        }

        if (companyRepository.count() > 0
                || employerRepository.count() > 0
                || userRepository.count() > 0
                || vacancyRepository.count() > 0) {
            log.info("Skipping demo data seeding because database is not empty");
            return;
        }

        log.info("Seeding demo data into empty database");

        Skill javaSkill = saveSkill("Java");
        Skill springSkill = saveSkill("Spring Boot");
        Skill postgresSkill = saveSkill("PostgreSQL");
        Skill reactSkill = saveSkill("React");
        Skill dockerSkill = saveSkill("Docker");

        Company techNova = new Company();
        techNova.setName("TechNova");
        techNova.setDescription("Product company building hiring and HR automation tools.");
        techNova.setIndustry("IT");
        techNova.setLocation("Minsk");
        techNova.setWebsite("https://technova.example");
        techNova.setContactEmail("hello@technova.example");
        techNova.setContactPhone("+375291110011");
        techNova = companyRepository.save(techNova);

        Company cloudForge = new Company();
        cloudForge.setName("CloudForge");
        cloudForge.setDescription("Engineering company focused on cloud platforms and B2B products.");
        cloudForge.setIndustry("Software");
        cloudForge.setLocation("Warsaw");
        cloudForge.setWebsite("https://cloudforge.example");
        cloudForge.setContactEmail("team@cloudforge.example");
        cloudForge.setContactPhone("+48221110022");
        cloudForge = companyRepository.save(cloudForge);

        Employer anna = new Employer();
        anna.setFirstName("Anna");
        anna.setLastName("Kovaleva");
        anna.setEmail("anna.kovaleva@technova.example");
        anna.setPhoneNumber("+375291110012");
        anna.setCompany(techNova);
        anna = employerRepository.save(anna);

        Employer pavel = new Employer();
        pavel.setFirstName("Pavel");
        pavel.setLastName("Orlov");
        pavel.setEmail("pavel.orlov@cloudforge.example");
        pavel.setPhoneNumber("+48221110023");
        pavel.setCompany(cloudForge);
        pavel = employerRepository.save(pavel);

        User ivan = new User();
        ivan.setFirstName("Ivan");
        ivan.setLastName("Petrov");
        ivan.setEmail("ivan.petrov@example.com");
        ivan.setPhoneNumber("+375291230001");
        ivan.setSkills(List.of(javaSkill, springSkill, postgresSkill, dockerSkill));
        ivan = userRepository.save(ivan);

        User maria = new User();
        maria.setFirstName("Maria");
        maria.setLastName("Sidorova");
        maria.setEmail("maria.sidorova@example.com");
        maria.setPhoneNumber("+375291230002");
        maria.setSkills(List.of(reactSkill, dockerSkill));
        maria = userRepository.save(maria);

        Resume ivanResume = new Resume();
        ivanResume.setTitle("Java Backend Developer");
        ivanResume.setSkills("Java, Spring Boot, PostgreSQL, Docker, REST API");
        ivanResume.setExperience("3 years of backend development, microservices and database design.");
        ivanResume.setEducation("BSU, Applied Informatics");
        ivanResume.setExpectedSalary(3500);
        ivanResume.setLocation("Minsk");
        ivanResume.setUser(ivan);
        ivanResume = resumeRepository.save(ivanResume);

        Resume mariaResume = new Resume();
        mariaResume.setTitle("Frontend React Developer");
        mariaResume.setSkills("React, JavaScript, CSS, Docker, UI integration");
        mariaResume.setExperience("2 years of frontend development, SPA dashboards and API integration.");
        mariaResume.setEducation("BNTU, Software Engineering");
        mariaResume.setExpectedSalary(2800);
        mariaResume.setLocation("Minsk");
        mariaResume.setUser(maria);
        mariaResume = resumeRepository.save(mariaResume);

        Vacancy backendVacancy = new Vacancy();
        backendVacancy.setTitle("Middle Java Developer");
        backendVacancy.setDescription("Build backend services for the job search platform and internal tools.");
        backendVacancy.setSalary(4200);
        backendVacancy.setRequiredExperience(2);
        backendVacancy.setLocation("Minsk");
        backendVacancy.setCreatedBy(anna);
        backendVacancy = vacancyRepository.save(backendVacancy);

        Vacancy frontendVacancy = new Vacancy();
        frontendVacancy.setTitle("Frontend React Engineer");
        frontendVacancy.setDescription("Create SPA interfaces, improve UX and integrate with REST APIs.");
        frontendVacancy.setSalary(3200);
        frontendVacancy.setRequiredExperience(2);
        frontendVacancy.setLocation("Remote");
        frontendVacancy.setCreatedBy(pavel);
        frontendVacancy = vacancyRepository.save(frontendVacancy);

        Application firstApplication = new Application();
        firstApplication.setVacancy(backendVacancy);
        firstApplication.setResume(ivanResume);
        firstApplication.setCoverLetter("I have strong Java and Spring Boot experience and would like to join TechNova.");
        firstApplication.setStatus(ApplicationStatus.PENDING);
        applicationRepository.save(firstApplication);

        Application secondApplication = new Application();
        secondApplication.setVacancy(frontendVacancy);
        secondApplication.setResume(mariaResume);
        secondApplication.setCoverLetter("I build React SPAs and enjoy product-oriented frontend development.");
        secondApplication.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(secondApplication);

        authAccountRepository.save(buildSeekerAccount("ivan", "ivan123", ivan));
        authAccountRepository.save(buildSeekerAccount("maria", "maria123", maria));
        authAccountRepository.save(buildEmployerAccount("anna.hr", "anna123", anna));
        authAccountRepository.save(buildEmployerAccount("pavel.hr", "pavel123", pavel));

        log.info("Demo data seeding finished successfully");
    }

    private Skill saveSkill(String name) {
        Skill skill = new Skill();
        skill.setName(name);
        return skillRepository.save(skill);
    }

    private AuthAccount buildSeekerAccount(String login, String rawPassword, User user) {
        AuthAccount account = new AuthAccount();
        account.setLogin(login);
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        account.setRole(AccountRole.SEEKER);
        account.setUser(user);
        return account;
    }

    private AuthAccount buildEmployerAccount(String login, String rawPassword, Employer employer) {
        AuthAccount account = new AuthAccount();
        account.setLogin(login);
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        account.setRole(AccountRole.EMPLOYER);
        account.setEmployer(employer);
        return account;
    }
}
