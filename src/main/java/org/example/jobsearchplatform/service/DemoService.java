package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;

    /**
     * Метод БЕЗ @Transactional.
     * Компания сохраняется всегда, даже если работодатель не сохранится.
     * Если email некорректен (формат или дубликат), выбрасывается исключение ПОСЛЕ сохранения компании.
     * Защита от дубликатов компании по имени в начале метода.
     */
    public void saveWithoutTransaction(DemoRequest request) {
        System.out.println("\n========== МЕТОД БЕЗ @Transactional ==========");

        // 1. Проверка дубликата компании
        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new RuntimeException("Company with name '" + request.getCompanyName() + "' already exists.");
        }

        // 2. Сохраняем компанию
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription("Компания из демо-запроса (без транзакции)");
        company.setIndustry("IT");
        company.setLocation("Москва");
        company.setWebsite("https://example.com");
        company.setContactEmail("contact@" + request.getCompanyName().toLowerCase().replace(" ", "") + ".com");

        Company savedCompany = companyRepository.save(company);
        System.out.println("✅ ШАГ 1: Компания сохранена! ID: " + savedCompany.getId());

        // 3. Валидация формата email (после сохранения компании)
        if (!isValidEmail(request.getEmployerEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmployerEmail());
        }

        // 4. Пытаемся сохранить работодателя
        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber("+79001234567");
        employer.setCompany(savedCompany);

        employerRepository.save(employer); // Здесь может выброситься DataIntegrityViol
        // ationException (если email уже существует)
        System.out.println("✅ ШАГ 2: Работодатель сохранен!");
    }

    /**
     * Метод С @Transactional.
     * Если возникает ошибка при сохранении работодателя (некорректный email или дубликат), всё откатывается.
     * Валидация формата email выполняется после сохранения компании, но до ком
     * мита – при ошибке транзакция откатывается.
     */
    @Transactional
    public void saveWithTransaction(DemoRequest request) {
        System.out.println("\n========== МЕТОД С @Transactional ==========");

        // 1. Проверка дубликата компании
        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new RuntimeException("Company with name '" + request.getCompanyName() + "' already exists.");
        }

        // 2. Сохраняем компанию
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription("Компания из демо-запроса (с транзакцией)");
        company.setIndustry("IT");
        company.setLocation("Москва");
        company.setWebsite("https://example.com");
        company.setContactEmail("contact@" + request.getCompanyName().toLowerCase().replace(" ", "") + ".com");

        Company savedCompany = companyRepository.save(company);
        System.out.println("✅ ШАГ 1: Компания сохранена (в контексте)!");

        // 3. Валидация формата email (после сохранения компании)
        if (!isValidEmail(request.getEmployerEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmployerEmail());
        }

        // 4. Пытаемся сохранить работодателя
        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber("+79001234567");
        employer.setCompany(savedCompany);

        employerRepository.save(employer); // При ошибке (дубликат email) транзакция откатится
        System.out.println("✅ ШАГ 2: Работодатель сохранен!");
    }

    /**
     * Успешное сохранение (с транзакцией) — для демонстрации.
     */
    @Transactional
    public void saveSuccessfully(DemoRequest request) {
        System.out.println("\n========== МЕТОД УСПЕШНОГО СОХРАНЕНИЯ ==========");

        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new RuntimeException("Company with name '" + request.getCompanyName() + "' already exists.");
        }

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription("Успешная компания");
        company.setIndustry("IT");
        company.setLocation("Москва");
        company.setWebsite("https://example.com");
        company.setContactEmail("hr@example.com");

        Company savedCompany = companyRepository.save(company);
        System.out.println("✅ ШАГ 1: Компания сохранена!");

        // Валидация формата email
        if (!isValidEmail(request.getEmployerEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmployerEmail());
        }

        Employer employer = new Employer();
        employer.setFirstName(request.getEmployerFirstName());
        employer.setLastName(request.getEmployerLastName());
        employer.setEmail(request.getEmployerEmail());
        employer.setPhoneNumber("+79001234567");
        employer.setCompany(savedCompany);

        Employer savedEmployer = employerRepository.save(employer);
        System.out.println("✅ ШАГ 2: Работодатель сохранен!");
    }

    // Простейшая проверка формата email
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}