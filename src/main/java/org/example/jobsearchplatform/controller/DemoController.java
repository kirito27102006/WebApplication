package org.example.jobsearchplatform.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.DemoRequest;
import org.example.jobsearchplatform.dto.DemoResponse;
import org.example.jobsearchplatform.service.DemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
public class DemoController {

    private final DemoService demoService;

    @PostMapping("/without-tx")
    public ResponseEntity<DemoResponse> demonstrateWithoutTransaction(@RequestBody DemoRequest request) {
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
            details.put("company", "✅ СОХРАНИЛАСЬ в базе (проверь таблицу companies)");
            details.put("employer", "❌ НЕ сохранился (ошибка: " + e.getMessage() + ")");
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
    public ResponseEntity<DemoResponse> demonstrateWithTransaction(@RequestBody DemoRequest request) {
        try {
            demoService.saveWithTransaction(request);
            return ResponseEntity.ok(DemoResponse.builder()
                    .success(true)
                    .message("✅ Все данные успешно сохранены с транзакцией!")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            Map<String, Object> details = new HashMap<>();
            details.put("company", "❌ НЕ сохранилась (откат транзакции)");
            details.put("employer", "❌ НЕ сохранился (ошибка: " + e.getMessage() + ")");
            details.put("error_message", e.getMessage());
            details.put("check_sql", "SELECT * FROM companies WHERE name = '" + request.getCompanyName() + "' (должен быть пустой результат)");

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
    public ResponseEntity<DemoResponse> demonstrateSuccess(@RequestBody DemoRequest request) {
        try {
            demoService.saveSuccessfully(request);
            Map<String, Object> details = new HashMap<>();
            details.put("company", "✅ Сохранена");
            details.put("employer", "✅ Сохранен");
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
}