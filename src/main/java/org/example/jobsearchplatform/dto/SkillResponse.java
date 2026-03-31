package org.example.jobsearchplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Skill response DTO")
public class SkillResponse {
    private Long id;
    private String name;
}
