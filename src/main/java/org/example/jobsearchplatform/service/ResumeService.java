package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ResumeCreateRequest;
import org.example.jobsearchplatform.dto.ResumeResponse;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.service.mapper.ResumeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private static final String RESUME_NOT_FOUND = "Resume not found with id: ";

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;

    public ResumeResponse createResume(ResumeCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Resume resume = resumeMapper.toEntity(request, user);
        Resume savedResume = resumeRepository.save(resume);
        return resumeMapper.toResponse(savedResume);
    }

    public ResumeResponse findById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RESUME_NOT_FOUND + id));
        return resumeMapper.toResponse(resume);
    }

    public List<ResumeResponse> findByUser(Long userId) {
        return resumeRepository.findByUserId(userId).stream()
                .map(resumeMapper::toResponse)
                .toList();
    }

    public List<ResumeResponse> searchResumes(String skill, String location, Integer maxSalary) {
        List<Resume> resumes;

        if (skill != null && !skill.isEmpty()) {
            resumes = resumeRepository.findBySkill(skill);
        } else if (location != null && !location.isEmpty()) {
            resumes = resumeRepository.findByLocationContainingIgnoreCase(location);
        } else if (maxSalary != null) {
            resumes = resumeRepository.findByExpectedSalaryLessThanEqual(maxSalary);
        } else {
            resumes = resumeRepository.findAll();
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
        resume.setStatus("HIDDEN");
        resumeRepository.save(resume);
    }

    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }
}