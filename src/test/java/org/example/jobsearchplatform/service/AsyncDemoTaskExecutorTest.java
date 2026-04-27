package org.example.jobsearchplatform.service;

import org.example.jobsearchplatform.dto.AsyncTaskStatusResponse;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.enums.AsyncTaskStatus;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AsyncDemoTaskExecutorTest {

    private CompanyRepository companyRepository;
    private EmployerRepository employerRepository;
    private AsyncTaskRegistryService asyncTaskRegistryService;
    private AsyncDemoTaskExecutor asyncDemoTaskExecutor;

    @BeforeEach
    void setUp() {
        companyRepository = mock(CompanyRepository.class);
        employerRepository = mock(EmployerRepository.class);
        asyncTaskRegistryService = new AsyncTaskRegistryService();

        DemoService demoService = new DemoService(companyRepository, employerRepository);
        asyncDemoTaskExecutor = new AsyncDemoTaskExecutor(demoService, asyncTaskRegistryService, 0);
    }

    @Test
    void execute_success_marksTaskCompleted() {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();
        DemoRequest request = buildRequest("AsyncCompany", "async.user@example.com");

        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        asyncDemoTaskExecutor.execute(taskState.getTaskId(), request).join();

        AsyncTaskStatusResponse response = asyncTaskRegistryService.getTaskStatus(taskState.getTaskId());
        assertEquals(AsyncTaskStatus.COMPLETED, response.getStatus());
        verify(companyRepository).save(any(Company.class));
        verify(employerRepository).save(any());
    }

    @Test
    void execute_failure_marksTaskFailed() {
        AsyncTaskState taskState = asyncTaskRegistryService.createTask();
        DemoRequest request = buildRequest("AsyncFailure", "invalid-email");

        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company saved = invocation.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        asyncDemoTaskExecutor.execute(taskState.getTaskId(), request).join();

        AsyncTaskStatusResponse response = asyncTaskRegistryService.getTaskStatus(taskState.getTaskId());
        assertEquals(AsyncTaskStatus.FAILED, response.getStatus());
        assertEquals("Invalid email format: invalid-email", response.getError());
    }

    private DemoRequest buildRequest(String companyName, String email) {
        DemoRequest request = new DemoRequest();
        request.setCompanyName(companyName);
        request.setEmployerEmail(email);
        request.setEmployerFirstName("Async");
        request.setEmployerLastName("User");
        return request;
    }
}
