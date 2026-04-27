package org.example.jobsearchplatform.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.AsyncTaskStartResponse;
import org.example.jobsearchplatform.dto.AsyncTaskStatusResponse;
import org.example.jobsearchplatform.dto.CounterConcurrencyDemoResponse;
import org.example.jobsearchplatform.dto.CounterResponse;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.dto.DemoResponse;
import org.example.jobsearchplatform.dto.RaceConditionDemoResponse;
import org.example.jobsearchplatform.service.AsyncDemoTaskService;
import org.example.jobsearchplatform.service.DemoService;
import org.example.jobsearchplatform.service.RaceConditionDemoService;
import org.example.jobsearchplatform.service.ThreadSafeCounterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Validated
@Tag(name = "Demo", description = "Demo endpoints for transaction behavior")
public class DemoController {

    private final DemoService demoService;
    private final AsyncDemoTaskService asyncDemoTaskService;
    private final ThreadSafeCounterService threadSafeCounterService;
    private final RaceConditionDemoService raceConditionDemoService;
    private static final String COMPANY = "company";
    private static final String EMPLOYER = "employer";

    @PostMapping("/without-tx")
    @Operation(summary = "Run demo without transaction")
    public ResponseEntity<DemoResponse> demonstrateWithoutTransaction(@Valid @RequestBody DemoRequest request) {
        try {
            demoService.saveWithoutTransaction(request);
            return ResponseEntity.ok(DemoResponse.builder()
                    .success(true)
                    .message("✅ Все данные успешно сохранены!")
                    .explanation("Компания и работодатель созданы (без транзакции)")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            Map<String, Object> details = new HashMap<>();
            details.put(COMPANY, "✅ СОХРАНИЛАСЬ в базе (проверь таблицу companies)");
            details.put(EMPLOYER, "❌ НЕ сохранился (ошибка: " + e.getMessage() + ")");
            details.put("error_message", e.getMessage());
            details.put("check_sql", "SELECT * FROM companies WHERE name = '" + request.getCompanyName() + "'");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(DemoResponse.builder()
                            .success(false)
                            .message("Частичное сохранение: компания создана, работодатель нет")
                            .error("Business Rule Violation")
                            .explanation("Метод БЕЗ @Transactional: компания сохранилась, работодатель - нет")
                            .details(details)
                            .timestamp(LocalDateTime.now())
                            .build());
        }
    }

    @PostMapping("/with-tx")
    @Operation(summary = "Run demo with transaction")
    public ResponseEntity<DemoResponse> demonstrateWithTransaction(@Valid @RequestBody DemoRequest request) {
        try {
            demoService.saveWithTransaction(request);
            return ResponseEntity.ok(DemoResponse.builder()
                    .success(true)
                    .message("✅ Все данные успешно сохранены с транзакцией!")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            Map<String, Object> details = new HashMap<>();
            details.put(COMPANY, "❌ НЕ сохранилась (откат транзакции)");
            details.put(EMPLOYER, "❌ НЕ сохранился (ошибка: " + e.getMessage() + ")");
            details.put("error_message", e.getMessage());
            details.put("check_sql", "SELECT * FROM companies WHERE name " +
                    "= '" + request.getCompanyName() + "' (должен быть пустой результат)");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(DemoResponse.builder()
                            .success(false)
                            .message("Транзакция откачена, ничего не сохранено")
                            .error("Business Rule Violation")
                            .explanation("Метод С @Transactional: все изменения откатились")
                            .details(details)
                            .timestamp(LocalDateTime.now())
                            .build());
        }
    }

    @PostMapping("/success")
    @Operation(summary = "Run successful demo scenario")
    public ResponseEntity<DemoResponse> demonstrateSuccess(@Valid @RequestBody DemoRequest request) {
        try {
            demoService.saveSuccessfully(request);
            Map<String, Object> details = new HashMap<>();
            details.put(COMPANY, "✅ Сохранена");
            details.put(EMPLOYER, "✅ Сохранен");
            return ResponseEntity.ok(DemoResponse.builder()
                    .success(true)
                    .message("✅ Все данные успешно сохранены!")
                    .explanation("Компания и работодатель созданы")
                    .details(details)
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DemoResponse.builder()
                            .success(false)
                            .message("❌ Ошибка при сохранении")
                            .error(e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build());
        }
    }

    @GetMapping("/check")
    @Operation(summary = "Get SQL checks for demo")
    public ResponseEntity<DemoResponse> checkResults() {
        Map<String, Object> details = new HashMap<>();
        details.put("without_tx_sql", "SELECT * FROM companies WHERE name LIKE 'Company_NoTx%'");
        details.put("with_tx_sql", "SELECT * FROM companies WHERE name LIKE 'Company_WithTx%'");
        details.put("success_sql", "SELECT * FROM companies WHERE name LIKE 'Company_Success%'");
        details.put("cleanup_sql", "DELETE FROM companies WHERE name LIKE 'Company_%'");
        return ResponseEntity.ok(DemoResponse.builder()
                .success(true)
                .message("🔍 SQL запросы для проверки результатов")
                .details(details)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/async")
    @Operation(summary = "Start async demo business operation")
    public ResponseEntity<AsyncTaskStartResponse> startAsyncOperation(@Valid @RequestBody DemoRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(asyncDemoTaskService.startTask(request));
    }

    @PostMapping("/async/no-delay")
    @Operation(summary = "Start async demo business operation without artificial delay")
    public ResponseEntity<AsyncTaskStartResponse> startAsyncOperationWithoutDelay(
            @Valid @RequestBody DemoRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(asyncDemoTaskService.startTaskWithoutDelay(request));
    }

    @GetMapping("/async/{taskId}")
    @Operation(summary = "Get async task execution status")
    public ResponseEntity<AsyncTaskStatusResponse> getAsyncOperationStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(asyncDemoTaskService.getTaskStatus(taskId));
    }

    @PostMapping("/counter/increment")
    @Operation(summary = "Increment thread-safe counter")
    public ResponseEntity<CounterResponse> incrementCounter() {
        long value = threadSafeCounterService.incrementAndGet();
        return ResponseEntity.ok(CounterResponse.builder()
                .value(value)
                .message("Counter incremented successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/counter")
    @Operation(summary = "Get current thread-safe counter value")
    public ResponseEntity<CounterResponse> getCounterValue() {
        long value = threadSafeCounterService.getValue();
        return ResponseEntity.ok(CounterResponse.builder()
                .value(value)
                .message("Current counter value")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/counter/reset")
    @Operation(summary = "Reset thread-safe counter")
    public ResponseEntity<CounterResponse> resetCounter() {
        long value = threadSafeCounterService.reset();
        return ResponseEntity.ok(CounterResponse.builder()
                .value(value)
                .message("Counter reset successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/counter/race-condition")
    @Operation(summary = "Demonstrate race condition and AtomicLong solution")
    public ResponseEntity<RaceConditionDemoResponse> demonstrateRaceCondition() {
        return ResponseEntity.ok(raceConditionDemoService.runDefaultDemo());
    }

    @GetMapping("/counter/race-condition/unsafe")
    @Operation(summary = "Demonstrate unsafe counter under concurrent access")
    public ResponseEntity<CounterConcurrencyDemoResponse> demonstrateUnsafeCounter() {
        return ResponseEntity.ok(raceConditionDemoService.runUnsafeDemo());
    }

    @GetMapping("/counter/race-condition/atomic")
    @Operation(summary = "Demonstrate AtomicLong counter under concurrent access")
    public ResponseEntity<CounterConcurrencyDemoResponse> demonstrateAtomicCounter() {
        return ResponseEntity.ok(raceConditionDemoService.runAtomicDemo());
    }

    @GetMapping("/counter/race-condition/synchronized")
    @Operation(summary = "Demonstrate synchronized counter under concurrent access")
    public ResponseEntity<CounterConcurrencyDemoResponse> demonstrateSynchronizedCounter() {
        return ResponseEntity.ok(raceConditionDemoService.runSynchronizedDemo());
    }
}
