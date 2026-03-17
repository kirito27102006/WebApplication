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
        executeSave(request,
                "Компания из демо-запроса (без транзакции)",
                buildContactEmail(request.getCompanyName())); // динамический email
    }

    @Transactional
    public void saveWithTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД С @Transactional ==========");
        executeSave(request,
                "Компания из демо-запроса (с транзакцией)",
                buildContactEmail(request.getCompanyName()));
    }

    @Transactional
    public void saveSuccessfully(DemoRequest request) {
        log.info("\n========== МЕТОД УСПЕШНОГО СОХРАНЕНИЯ ==========");
        executeSave(request, "Успешная компания", "hr@example.com");
    }

    private void executeSave(DemoRequest request, String companyDescription, String contactEmail) {
        checkCompanyNotExists(request.getCompanyName());
        Company company = buildCompany(request, companyDescription, contactEmail);
        Company savedCompany = saveCompany(company);
        log.info("✅ ШАГ 1: Компания сохранена! ID: {}", savedCompany.getId());

        validateEmail(request.getEmployerEmail());

        Employer employer = buildEmployer(request, savedCompany);
        saveEmployer(employer);
        log.info(EMPLOYER_SAVED_MSG);
    }

    private void checkCompanyNotExists(String companyName) {
        if (companyRepository.existsByName(companyName)) {
            throw new IllegalArgumentException("Company with name '" + companyName + "' already exists.");
        }
    }

    private Company buildCompany(DemoRequest request, String description, String contactEmail) {
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription(description);
        company.setIndustry("IT");
        company.setLocation(DEFAULT_LOCATION);
        company.setWebsite(DEFAULT_WEBSITE);
        company.setContactEmail(contactEmail);
        return company;
    }

    private Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    private void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
    }

    private Employer buildEmployer(DemoRequest request, Company company) {
        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber(DEFAULT_PHONE);
        employer.setCompany(company);
        return employer;
    }

    private void saveEmployer(Employer employer) {
        employerRepository.save(employer);
    }

    private String buildContactEmail(String companyName) {
        return "contact@" + companyName.toLowerCase().replace(" ", "") + ".com";
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}