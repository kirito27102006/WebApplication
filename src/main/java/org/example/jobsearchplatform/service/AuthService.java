package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.jobsearchplatform.dto.AuthLoginRequest;
import org.example.jobsearchplatform.dto.AuthRegisterRequest;
import org.example.jobsearchplatform.dto.AuthResponse;
import org.example.jobsearchplatform.dto.CompanyCreateRequest;
import org.example.jobsearchplatform.dto.CompanyResponse;
import org.example.jobsearchplatform.exception.UnauthorizedException;
import org.example.jobsearchplatform.model.AuthAccount;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.model.Employer;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.enums.AccountRole;
import org.example.jobsearchplatform.repository.AuthAccountRepository;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.repository.EmployerRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.service.CompanyService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthAccountRepository authAccountRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, SessionPrincipal> sessions = new ConcurrentHashMap<>();

    public AuthResponse register(AuthRegisterRequest request) {
        if (authAccountRepository.existsByLogin(request.getLogin())) {
            throw new IllegalArgumentException("Login '" + request.getLogin() + "' is already taken");
        }

        return request.getRole() == AccountRole.SEEKER
                ? registerSeeker(request)
                : registerEmployer(request);
    }

    public AuthResponse login(AuthLoginRequest request) {
        AuthAccount account = authAccountRepository.findByLoginAndRole(request.getLogin(), request.getRole())
                .orElseThrow(() -> new UnauthorizedException("Invalid login, password or role"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new UnauthorizedException("Invalid login, password or role");
        }

        return createSessionResponse(account);
    }

    public CompanyResponse createCompanyForRegistration(CompanyCreateRequest request) {
        return companyService.createCompany(request);
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentSession(String token) {
        SessionPrincipal principal = resolve(token);
        return AuthResponse.builder()
                .token(token)
                .login(principal.getLogin())
                .role(principal.getRole())
                .displayName(principal.getDisplayName())
                .userId(principal.getUserId())
                .employerId(principal.getEmployerId())
                .companyId(principal.getCompanyId())
                .build();
    }

    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            sessions.remove(token);
        }
    }

    public SessionPrincipal resolve(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Authentication is required");
        }

        SessionPrincipal principal = sessions.get(token);
        if (principal == null) {
            throw new UnauthorizedException("Authentication session is missing or expired");
        }

        return principal;
    }

    private AuthResponse registerSeeker(AuthRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        User savedUser = userRepository.save(user);

        AuthAccount account = new AuthAccount();
        account.setLogin(request.getLogin());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(AccountRole.SEEKER);
        account.setUser(savedUser);
        AuthAccount savedAccount = authAccountRepository.save(account);
        return createSessionResponse(savedAccount);
    }

    private AuthResponse registerEmployer(AuthRegisterRequest request) {
        if (request.getCompanyId() == null) {
            throw new IllegalArgumentException("Company is required for employer registration");
        }
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Employer with email " + request.getEmail() + " already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + request.getCompanyId()));

        Employer employer = new Employer();
        employer.setFirstName(request.getFirstName());
        employer.setLastName(request.getLastName());
        employer.setEmail(request.getEmail());
        employer.setPhoneNumber(request.getPhoneNumber());
        employer.setCompany(company);
        Employer savedEmployer = employerRepository.save(employer);

        AuthAccount account = new AuthAccount();
        account.setLogin(request.getLogin());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(AccountRole.EMPLOYER);
        account.setEmployer(savedEmployer);
        AuthAccount savedAccount = authAccountRepository.save(account);
        return createSessionResponse(savedAccount);
    }

    private AuthResponse createSessionResponse(AuthAccount account) {
        String token = UUID.randomUUID().toString();
        SessionPrincipal principal = toPrincipal(account);
        sessions.put(token, principal);

        return AuthResponse.builder()
                .token(token)
                .login(principal.getLogin())
                .role(principal.getRole())
                .displayName(principal.getDisplayName())
                .userId(principal.getUserId())
                .employerId(principal.getEmployerId())
                .companyId(principal.getCompanyId())
                .build();
    }

    private SessionPrincipal toPrincipal(AuthAccount account) {
        if (account.getRole() == AccountRole.SEEKER && account.getUser() != null) {
            return SessionPrincipal.builder()
                    .accountId(account.getId())
                    .login(account.getLogin())
                    .role(account.getRole())
                    .displayName(account.getUser().getFirstName() + " " + account.getUser().getLastName())
                    .userId(account.getUser().getId())
                    .build();
        }

        Employer employer = account.getEmployer();
        return SessionPrincipal.builder()
                .accountId(account.getId())
                .login(account.getLogin())
                .role(account.getRole())
                .displayName(employer.getFirstName() + " " + employer.getLastName())
                .employerId(employer.getId())
                .companyId(employer.getCompany() != null ? employer.getCompany().getId() : null)
                .build();
    }

    @Value
    @Builder
    public static class SessionPrincipal {
        Long accountId;
        String login;
        AccountRole role;
        String displayName;
        Long userId;
        Long employerId;
        Long companyId;
    }
}
