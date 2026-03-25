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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoService {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;

    private static final String DEFAULT_LOCATION = "Москва";
    private static final String DEFAULT_WEBSITE = "https://example.com";
    private static final String DEFAULT_PHONE = "+79001234567";

    /**
     * Метод БЕЗ @Transactional на уровне сервиса.
     * Каждое сохранение выполняется в отдельной транзакции.
     * Если после сохранения компании произойдёт ошибка, компания останется в БД.
     */
    public void saveWithoutTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД БЕЗ @Transactional ==========");
        DemoRequest uniqueRequest = prepareUniqueRequest(request);
        executeSave(uniqueRequest, "Компания из демо-запроса (без транзакции)");
    }

    @Transactional
    public void saveWithTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД С @Transactional ==========");
        DemoRequest uniqueRequest = prepareUniqueRequest(request);
        executeSave(uniqueRequest, "Компания из демо-запроса (с транзакцией)");
    }

    @Transactional
    public void saveSuccessfully(DemoRequest request) {
        log.info("\n========== МЕТОД УСПЕШНОГО СОХРАНЕНИЯ ==========");
        DemoRequest uniqueRequest = prepareUniqueRequest(request);
        executeSave(uniqueRequest, "Успешная компания");
    }

    /**
     * Создаёт копию запроса с уникальным именем компании (добавляет timestamp).
     */
    private DemoRequest prepareUniqueRequest(DemoRequest original) {
        String uniqueName = generateUniqueName(original.getCompanyName());
        DemoRequest copy = new DemoRequest();
        copy.setCompanyName(uniqueName);
        copy.setEmployerEmail(original.getEmployerEmail());
        copy.setEmployerFirstName(original.getEmployerFirstName());
        copy.setEmployerLastName(original.getEmployerLastName());
        return copy;
    }

    private void executeSave(DemoRequest request, String companyDescription) {
        // Шаг 1: проверяем, что компании с таким именем ещё нет (уникальное имя гарантирует отсутствие)
        checkCompanyNotExists(request.getCompanyName());

        // Шаг 2: создаём и сохраняем компанию
        Company company = buildCompany(request, companyDescription);
        Company savedCompany = saveCompany(company);
        log.info("✅ ШАГ 1: Компания сохранена! ID: {}, название: {}", savedCompany.getId(), savedCompany.getName());

        // Шаг 3: проверяем email работодателя (может выбросить исключение)
        validateEmail(request.getEmployerEmail());

        // Шаг 4: создаём и сохраняем работодателя
        Employer employer = buildEmployer(request, savedCompany);
        saveEmployer(employer);
        log.info("✅ ШАГ 2: Работодатель сохранён! Email: {}", employer.getEmail());
    }

    private void checkCompanyNotExists(String companyName) {
        if (companyRepository.existsByName(companyName)) {
            throw new IllegalArgumentException("Company with name '" + companyName + "' already exists.");
        }
    }

    private Company buildCompany(DemoRequest request, String description) {
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription(description);
        company.setIndustry("IT");
        company.setLocation(DEFAULT_LOCATION);
        company.setWebsite(DEFAULT_WEBSITE);
        company.setContactEmail(buildContactEmail(request.getCompanyName()));
        return company;
    }

    private Company saveCompany(Company company) {
        Company saved = companyRepository.save(company);
        // Принудительно отправляем данные в БД, чтобы гарантировать немедленную запись
        companyRepository.flush();
        return saved;
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
        employerRepository.flush();
    }

    private String buildContactEmail(String companyName) {
        return "contact@" + companyName.toLowerCase().replace(" ", "") + ".com";
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Генерирует уникальное имя компании, добавляя timestamp.
     */
    private String generateUniqueName(String baseName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return baseName + "_" + timestamp;
    }
}