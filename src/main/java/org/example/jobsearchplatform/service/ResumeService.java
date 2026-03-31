package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.ResumeCreateRequest;
import org.example.jobsearchplatform.dto.ResumeResponse;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.enums.ResumeStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.service.mapper.ResumeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private static final String RESUME_NOT_FOUND = "Resume not found with id: ";

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ResumeMapper resumeMapper;

    @Transactional(readOnly = true)
    public List<ResumeResponse> findAll() {
        log.info("Fetching all resumes with user data");
        return resumeRepository.findAllWithUser().stream()
                .map(resumeMapper::toResponse)
                .toList();
    }

    public ResumeResponse createResume(ResumeCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Resume resume = resumeMapper.toEntity(request, user);
        Resume savedResume = resumeRepository.save(resume);
        return resumeMapper.toResponse(savedResume);
    }

    @Transactional(readOnly = true)
    public ResumeResponse findById(Long id) {
        Resume resume = resumeRepository.findByIdWithUser(id)
                .orElseThrow(() -> new EntityNotFoundException(RESUME_NOT_FOUND + id));
        return resumeMapper.toResponse(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        return resumeRepository.findByUserIdWithUser(userId).stream()
                .map(resumeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> searchResumes(String skill, String location, Integer maxSalary) {
        List<Resume> resumes;

        if (skill != null && !skill.isEmpty()) {
            resumes = resumeRepository.findBySkillWithUser(skill);
        } else if (location != null && !location.isEmpty()) {
            resumes = resumeRepository.findByLocationWithUser(location);
        } else if (maxSalary != null) {
            resumes = resumeRepository.findByMaxSalaryWithUser(maxSalary);
        } else {
            resumes = resumeRepository.findAllWithUser();
        }

        return resumes.stream()
                .map(resumeMapper::toResponse)
                .toList();
    }

    public ResumeResponse updateResume(Long id, ResumeCreateRequest request) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RESUME_NOT_FOUND + id));

        resume.setTitle(request.getTitle());
        resume.setSkills(request.getSkills());
        resume.setExperience(request.getExperience());
        resume.setEducation(request.getEducation());
        resume.setExpectedSalary(request.getExpectedSalary());
        resume.setLocation(request.getLocation());

        Resume updatedResume = resumeRepository.save(resume);
        return resumeMapper.toResponse(updatedResume);
    }

    public void hideResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RESUME_NOT_FOUND + id));
        resume.setStatus(ResumeStatus.HIDDEN);
    }

    public void deleteResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RESUME_NOT_FOUND + id));

        if (applicationRepository.existsByResumeId(id)) {
            throw new IllegalStateException("Cannot delete resume because it has associated applications");
        }

        resumeRepository.delete(resume);
    }
}
