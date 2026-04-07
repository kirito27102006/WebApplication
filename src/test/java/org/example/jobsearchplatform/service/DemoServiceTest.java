package org.example.jobsearchplatform.service;

import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemoServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private EmployerRepository employerRepository;

    private DemoService demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoService(companyRepository, employerRepository);
    }

    @Test
    void saveWithoutTransaction_invalidEmail_companySavedButEmployerNotSaved() {
        DemoRequest request = new DemoRequest();
        request.setCompanyName("CompanyNoTx");
        request.setEmployerEmail("invalid-email");
        request.setEmployerFirstName("Demo");
        request.setEmployerLastName("User");

        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        assertThrows(IllegalArgumentException.class, () -> demoService.saveWithoutTransaction(request));

        verify(companyRepository).save(any(Company.class));
        verify(companyRepository).flush();
        verify(employerRepository, never()).save(any());
    }

    @Test
    void saveSuccessfully_validRequest_savesCompanyAndEmployer() {
        DemoRequest request = new DemoRequest();
        request.setCompanyName("CompanySuccess");
        request.setEmployerEmail("demo.user@example.com");
        request.setEmployerFirstName("Demo");
        request.setEmployerLastName("User");

        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        demoService.saveSuccessfully(request);

        verify(companyRepository).save(any(Company.class));
        verify(companyRepository).flush();
        verify(employerRepository).save(any());
        verify(employerRepository).flush();
    }

    @Test
    void saveWithTransaction_invalidEmail_throwsAfterCompanySave() {
        DemoRequest request = new DemoRequest();
        request.setCompanyName("CompanyTx");
        request.setEmployerEmail("invalid-email");
        request.setEmployerFirstName("Demo");
        request.setEmployerLastName("User");

        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        assertThrows(IllegalArgumentException.class, () -> demoService.saveWithTransaction(request));

        verify(companyRepository).save(any(Company.class));
        verify(companyRepository).flush();
        verify(employerRepository, never()).save(any());
    }

    @Test
    void saveWithoutTransaction_companyAlreadyExists_throws() {
        DemoRequest request = new DemoRequest();
        request.setCompanyName("Duplicate");
        request.setEmployerEmail("demo.user@example.com");
        request.setEmployerFirstName("Demo");
        request.setEmployerLastName("User");

        when(companyRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> demoService.saveWithoutTransaction(request));

        verify(companyRepository, never()).save(any());
        verify(employerRepository, never()).save(any());
    }
}
