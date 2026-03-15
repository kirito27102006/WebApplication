package org.example.jobsearchplatform.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoResponse {
    private boolean success;
    private String message;
    private String error;
    private String explanation;
    private Map<String, Object> details;
    private LocalDateTime timestamp;
}