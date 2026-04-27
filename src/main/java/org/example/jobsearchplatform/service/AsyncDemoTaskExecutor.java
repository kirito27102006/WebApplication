package org.example.jobsearchplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncDemoTaskExecutor {

    private final DemoService demoService;
    private final AsyncTaskRegistryService asyncTaskRegistryService;
    private final long artificialDelayMillis;

    public AsyncDemoTaskExecutor(
            DemoService demoService,
            AsyncTaskRegistryService asyncTaskRegistryService,
            @Value("${app.async.demo-delay-ms:3000}") long artificialDelayMillis) {
        this.demoService = demoService;
        this.asyncTaskRegistryService = asyncTaskRegistryService;
        this.artificialDelayMillis = artificialDelayMillis;
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Void> execute(String taskId, DemoRequest request) {
        return executeInternal(taskId, request, true);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Void> executeWithoutDelay(String taskId, DemoRequest request) {
        return executeInternal(taskId, request, false);
    }

    private CompletableFuture<Void> executeInternal(String taskId, DemoRequest request, boolean withDelay) {
        asyncTaskRegistryService.markRunning(taskId);
        try {
            if (withDelay) {
                pauseForDemo();
            }
            demoService.saveSuccessfully(request);
            asyncTaskRegistryService.markCompleted(taskId);
            log.info("Async demo task {} completed successfully", taskId);
        } catch (Exception ex) {
            asyncTaskRegistryService.markFailed(taskId, ex.getMessage());
            log.error("Async demo task {} failed: {}", taskId, ex.getMessage(), ex);
        }
        return CompletableFuture.completedFuture(null);
    }

    private void pauseForDemo() throws InterruptedException {
        if (artificialDelayMillis > 0) {
            Thread.sleep(artificialDelayMillis);
        }
    }
}
