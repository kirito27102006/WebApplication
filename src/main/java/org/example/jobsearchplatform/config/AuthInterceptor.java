package org.example.jobsearchplatform.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.model.enums.AccountRole;
import org.example.jobsearchplatform.service.AuthService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (!path.startsWith("/api/")) {
            return true;
        }

        if (path.startsWith("/api/auth/")) {
            return true;
        }

        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method)) {
            return true;
        }

        String token = extractToken(request.getHeader("Authorization"));
        AuthService.SessionPrincipal principal = authService.resolve(token);
        validateRole(path, method, principal.getRole());
        request.setAttribute("authPrincipal", principal);
        return true;
    }

    private void validateRole(String path, String method, AccountRole role) {
        if (path.startsWith("/api/users") || path.startsWith("/api/resumes")) {
            requireRole(role, AccountRole.SEEKER);
            return;
        }

        if (path.startsWith("/api/companies") || path.startsWith("/api/employers") || path.startsWith("/api/vacancies")) {
            requireRole(role, AccountRole.EMPLOYER);
            return;
        }

        if (path.startsWith("/api/applications")) {
            if (path.contains("/status")) {
                requireRole(role, AccountRole.EMPLOYER);
                return;
            }

            if ("POST".equalsIgnoreCase(method) || path.contains("/cancel")) {
                requireRole(role, AccountRole.SEEKER);
                return;
            }

            if ("DELETE".equalsIgnoreCase(method)) {
                return;
            }
        }
    }

    private void requireRole(AccountRole actualRole, AccountRole expectedRole) {
        if (actualRole != expectedRole) {
            throw new SecurityException("This action is available only for role " + expectedRole.name());
        }
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }

        return authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7).trim()
                : authorizationHeader.trim();
    }
}
