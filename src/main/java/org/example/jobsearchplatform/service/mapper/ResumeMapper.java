package org.example.jobsearchplatform.service.mapper;

import org.example.jobsearchplatform.dto.ResumeCreateRequest;
import org.example.jobsearchplatform.dto.ResumeResponse;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.User;
import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {

    public Resume toEntity(ResumeCreateRequest request, User user) {
        Resume resume = new Resume();
        resume.setTitle(request.getTitle());
        resume.setSkills(request.getSkills());
        resume.setExperience(request.getExperience());
        resume.setEducation(request.getEducation());
        resume.setExpectedSalary(request.getExpectedSalary());
        resume.setLocation(request.getLocation());
        resume.setUser(user);
        return resume;
    }

    public ResumeResponse toResponse(Resume resume) {
        ResumeResponse response = new ResumeResponse();
        response.setId(resume.getId());
        response.setTitle(resume.getTitle());
        response.setSkills(resume.getSkills());
        response.setExperience(resume.getExperience());
        response.setEducation(resume.getEducation());
        response.setExpectedSalary(resume.getExpectedSalary());
        response.setLocation(resume.getLocation());
        response.setStatus(resume.getStatus().name());
        response.setCreatedAt(resume.getCreatedAt());
        response.setUpdatedAt(resume.getUpdatedAt());

        if (resume.getUser() != null) {
            response.setUserId(resume.getUser().getId());
            response.setUserFullName(resume.getUser().getFirstName() + " " + resume.getUser().getLastName());
        }

        return response;
    }
}