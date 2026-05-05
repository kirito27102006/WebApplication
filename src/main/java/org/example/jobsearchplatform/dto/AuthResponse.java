package org.example.jobsearchplatform.dto;

import lombok.Builder;
import lombok.Value;
import org.example.jobsearchplatform.model.enums.AccountRole;

@Value
@Builder
public class AuthResponse {
    String token;
    String login;
    AccountRole role;
    String displayName;
    Long userId;
    Long employerId;
    Long companyId;
}
