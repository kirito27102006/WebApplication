package org.example.jobsearchplatform.service.mapper;

import org.example.jobsearchplatform.dto.SkillResponse;
import org.example.jobsearchplatform.dto.UserCreateRequest;
import org.example.jobsearchplatform.dto.UserResponse;
import org.example.jobsearchplatform.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        return user;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setResumesCount(user.getResumes().size());
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            List<SkillResponse> skillResponses = user.getSkills().stream()
                    .map(skill -> {
                        SkillResponse sr = new SkillResponse();
                        sr.setId(skill.getId());
                        sr.setName(skill.getName());
                        return sr;
                    })
                    .toList();
            response.setSkills(skillResponses);
        }
        return response;
    }
}