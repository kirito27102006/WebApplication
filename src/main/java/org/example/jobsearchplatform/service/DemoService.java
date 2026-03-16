package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoService {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private static final String DEFAULT_LOCATION = "Москва";
    private static final String DEFAULT_WEBSITE = "https://example.com";
    private static final String DEFAULT_PHONE = "+79001234567";
    private static final String EMPLOYER_SAVED_MSG = "✅ ШАГ 2: Работодатель сохранен!";

    public void saveWithoutTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД БЕЗ @Transactional ==========");

        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new IllegalArgumentException("Company with name '" + request.getCompanyName() + "' already exists.");
        }

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription("Компания из демо-запроса (без транзакции)");
        company.setIndustry("IT");
        company.setLocation(DEFAULT_LOCATION);
        company.setWebsite(DEFAULT_WEBSITE);
        company.setContactEmail("contact@" + request.getCompanyName().toLowerCase().replace(" ", "") + ".com");

        Company savedCompany = companyRepository.save(company);
        log.info("✅ ШАГ 1: Компания сохранена! ID: {}", savedCompany.getId());

        if (!isValidEmail(request.getEmployerEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmployerEmail());
        }

        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber(DEFAULT_PHONE);
        employer.setCompany(savedCompany);

        employerRepository.save(employer);
        log.info(EMPLOYER_SAVED_MSG);
    }

    @Transactional
    public void saveWithTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД С @Transactional ==========");

        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new IllegalArgumentException("Company with name '" + request.getCompanyName() + "' already exists.");
        }

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription("Компания из демо-запроса (с транзакцией)");
        company.setIndustry("IT");
        company.setLocation(DEFAULT_LOCATION);
        company.setWebsite(DEFAULT_WEBSITE);
        company.setContactEmail("contact@" + request.getCompanyName().toLowerCase().replace(" ", "") + ".com");

        Company savedCompany = companyRepository.save(company);
        log.info("✅ ШАГ 1: Компания сохранена (в контексте)!");

        if (!isValidEmail(request.getEmployerEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmployerEmail());
        }

        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber(DEFAULT_PHONE);
        employer.setCompany(savedCompany);

        employerRepository.save(employer);
        log.info(EMPLOYER_SAVED_MSG);
    }

    @Transactional
    public void saveSuccessfully(DemoRequest request) {
        log.info("\n========== МЕТОД УСПЕШНОГО СОХРАНЕНИЯ ==========");

        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new IllegalArgumentException("Company with name '" + request.getCompanyName() + "' already exists.");
        }

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription("Успешная компания");
        company.setIndustry("IT");
        company.setLocation(DEFAULT_LOCATION);
        company.setWebsite(DEFAULT_WEBSITE);
        company.setContactEmail("hr@example.com");

        Company savedCompany = companyRepository.save(company);
        log.info("✅ ШАГ 1: Компания сохранена!");

        if (!isValidEmail(request.getEmployerEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmployerEmail());
        }

        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber(DEFAULT_PHONE);
        employer.setCompany(savedCompany);

        employerRepository.save(employer);
        log.info(EMPLOYER_SAVED_MSG);
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}