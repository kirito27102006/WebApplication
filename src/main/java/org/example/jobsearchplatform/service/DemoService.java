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

    // Константы для устранения дублирования литералов
    private static final String DEFAULT_LOCATION = "Москва";
    private static final String DEFAULT_WEBSITE = "https://example.com";
    private static final String DEFAULT_PHONE = "+79001234567";
    private static final String EMPLOYER_SAVED_MSG = "✅ ШАГ 2: Работодатель сохранен!";

    // ==================== Публичные методы (точки входа) ====================

    /**
     * Метод БЕЗ @Transactional.
     * Компания сохраняется всегда, даже если работодатель не сохранится.
     * Если email некорректен (формат или дубликат), выбрасывается исключение ПОСЛЕ сохранения компании.
     */
    public void saveWithoutTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД БЕЗ @Transactional ==========");
        executeSave(request,
                "Компания из демо-запроса (без транзакции)",
                buildContactEmail(request.getCompanyName())); // динамический email
    }

    /**
     * Метод С @Transactional.
     * Если возникает ошибка при сохранении работодателя (некорректный email или дубликат), всё откатывается.
     * Валидация формата email выполняется после сохранения компании, но до коммита.
     */
    @Transactional
    public void saveWithTransaction(DemoRequest request) {
        log.info("\n========== МЕТОД С @Transactional ==========");
        executeSave(request,
                "Компания из демо-запроса (с транзакцией)",
                buildContactEmail(request.getCompanyName()));
    }

    /**
     * Успешное сохранение (с транзакцией) — для демонстрации.
     */
    @Transactional
    public void saveSuccessfully(DemoRequest request) {
        log.info("\n========== МЕТОД УСПЕШНОГО СОХРАНЕНИЯ ==========");
        // Для успешного сценария contactEmail фиксированный, не вычисляемый
        executeSave(request, "Успешная компания", "hr@example.com");
    }

    // ==================== Вспомогательные приватные методы ====================

    /**
     * Общая последовательность операций для всех трёх методов.
     * @param request данные запроса
     * @param companyDescription описание компании (разное для каждого метода)
     * @param contactEmail контактный email компании
     */
    private void executeSave(DemoRequest request, String companyDescription, String contactEmail) {
        // 1. Проверка дубликата компании
        checkCompanyNotExists(request.getCompanyName());

        // 2. Создание и сохранение компании
        Company company = buildCompany(request, companyDescription, contactEmail);
        Company savedCompany = saveCompany(company);
        log.info("✅ ШАГ 1: Компания сохранена! ID: {}", savedCompany.getId());

        // 3. Валидация формата email работодателя
        validateEmail(request.getEmployerEmail());

        // 4. Создание и сохранение работодателя
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

    // Простейшая проверка формата email
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}