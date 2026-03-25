package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ApplicationCreateRequest;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.example.jobsearchplatform.model.Application;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.model.enums.ApplicationStatus;
import org.example.jobsearchplatform.model.enums.VacancyStatus;
import org.example.jobsearchplatform.repository.ApplicationNativeSearchView;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.ApplicationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private static final String APPLICATION_NOT_FOUND = "Application not found with id: ";

    private final ApplicationRepository applicationRepository;
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;
    private final ApplicationSearchIndex applicationSearchIndex = new ApplicationSearchIndex();

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> findAll(Pageable pageable) {
        ApplicationSearchCacheKey cacheKey = new ApplicationSearchCacheKey(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString()
        );

        Page<ApplicationResponse> cachedApplications = applicationSearchIndex.get(cacheKey);
        if (cachedApplications != null) {
            return cachedApplications;
        }

        Page<ApplicationResponse> applicationsPage = applicationRepository.findAllWithJoins(pageable)
                .map(applicationMapper::toResponse);
        applicationSearchIndex.put(cacheKey, applicationsPage);
        return applicationsPage;
    }

    public ApplicationResponse createApplication(ApplicationCreateRequest request) {
        Vacancy vacancy = vacancyRepository.findById(request.getVacancyId())
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found with id: " + request.getVacancyId()));

        if (vacancy.getStatus() != VacancyStatus.ACTIVE) {
            throw new IllegalStateException("Cannot apply to a vacancy that is not ACTIVE");
        }

        Resume resume = resumeRepository.findByIdWithUser(request.getResumeId())
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id: " + request.getResumeId()));

        if (resume.getUser() == null) {
            throw new IllegalStateException("Cannot use this resume because the associated user has been deleted");
        }

        if (!resume.getUser().getId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Resume does not belong to the user");
        }

        if (applicationRepository.existsByUserIdAndVacancyId(request.getUserId(), request.getVacancyId())) {
            throw new IllegalStateException("User has already applied to this vacancy");
        }

        Application application = new Application();
        application.setVacancy(vacancy);
        application.setResume(resume);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(ApplicationStatus.PENDING);

        Application saved = applicationRepository.save(application);
        applicationSearchIndex.clear();
        return applicationMapper.toResponse(saved);
    }

    public ApplicationResponse findById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(APPLICATION_NOT_FOUND + id));
        return applicationMapper.toResponse(application);
    }

    public List<ApplicationResponse> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        return applicationRepository.findByUserIdWithJoins(userId).stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    public List<ApplicationResponse> findByVacancy(Long vacancyId) {
        if (!vacancyRepository.existsById(vacancyId)) {
            throw new EntityNotFoundException("Vacancy not found with id: " + vacancyId);
        }
        return applicationRepository.findByVacancyIdWithJoins(vacancyId).stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    public List<ApplicationResponse> searchByFiltersJpql(Long userId,
                                                         String status,
                                                         String vacancyTitle,
                                                         String resumeTitle) {
        return applicationRepository.searchByFiltersJpql(
                        userId,
                        parseStatus(status),
                        normalizeJpqlFilter(vacancyTitle),
                        normalizeJpqlFilter(resumeTitle)
                ).stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    public List<ApplicationResponse> searchByFiltersNative(Long userId,
                                                           String status,
                                                           String vacancyTitle,
                                                           String resumeTitle) {
        return applicationRepository.searchByFiltersNative(
                        userId,
                        normalizeStatus(status),
                        normalizeFilter(vacancyTitle),
                        normalizeFilter(resumeTitle)
                ).stream()
                .map(this::mapNativeSearchViewToResponse)
                .toList();
    }

    public ApplicationResponse updateStatus(Long id, String newStatus) {
        ApplicationStatus status = parseStatus(newStatus);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(APPLICATION_NOT_FOUND + id));
        application.setStatus(status);
        applicationSearchIndex.clear();
        return applicationMapper.toResponse(application);
    }

    public void cancelApplication(Long id, Long userId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(APPLICATION_NOT_FOUND + id));

        if (application.getResume() == null || application.getResume().getUser() == null) {
            throw new IllegalStateException("Cannot cancel application because the associated user no longer exists");
        }

        if (!application.getResume().getUser().getId().equals(userId)) {
            throw new SecurityException("You can only cancel your own applications");
        }

        application.setStatus(ApplicationStatus.CANCELLED);
        applicationSearchIndex.clear();
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
        applicationSearchIndex.clear();
    }

    private ApplicationStatus parseStatus(String status) {
        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null) {
            return null;
        }
        try {
            return ApplicationStatus.valueOf(normalizedStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return status.trim().toUpperCase();
    }

    private String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeJpqlFilter(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }

    private ApplicationResponse mapNativeSearchViewToResponse(ApplicationNativeSearchView view) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(view.getId());
        response.setUserId(view.getUserId());
        response.setUserFullName(view.getUserFullName());
        response.setVacancyId(view.getVacancyId());
        response.setVacancyTitle(view.getVacancyTitle());
        response.setResumeId(view.getResumeId());
        response.setResumeTitle(view.getResumeTitle());
        response.setCoverLetter(view.getCoverLetter());
        response.setStatus(view.getStatus());
        response.setCreatedAt(view.getCreatedAt());
        return response;
    }
}
