package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response for demo transaction endpoints")
public class DemoResponse {
    private boolean success;
    private String message;
    private String error;
    private String explanation;
    private Map<String, Object> details;
    private LocalDateTime timestamp;
}
