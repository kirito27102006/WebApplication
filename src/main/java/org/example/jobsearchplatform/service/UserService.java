package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.UserCreateRequest;
import org.example.jobsearchplatform.dto.UserResponse;
import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.enums.ResumeStatus;
import org.example.jobsearchplatform.model.enums.UserStatus;
import org.example.jobsearchplatform.repository.ApplicationRepository;
import org.example.jobsearchplatform.repository.ResumeRepository;
import org.example.jobsearchplatform.repository.UserRepository;
import org.example.jobsearchplatform.service.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String USER_NOT_FOUND_ID = "User not found with id: ";

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationRepository applicationRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ID + id));
        return userMapper.toResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponse> findByStatus(String status) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            return userRepository.findByStatus(userStatus).stream()
                    .map(userMapper::toResponse)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    public UserResponse updateUser(Long id, UserCreateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ID + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ID + id));
        user.setStatus(UserStatus.BLOCKED);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ID + id));

        List<Resume> userResumes = resumeRepository.findByUserId(id);

        boolean hasApplications = userResumes.stream()
                .anyMatch(resume -> applicationRepository.existsByResumeId(resume.getId()));

        if (hasApplications) {
            log.info("User {} has applications – performing soft delete", id);
            user.setStatus(UserStatus.DELETED);

            for (Resume resume : userResumes) {
                resume.setStatus(ResumeStatus.USER_DELETED);
            }

        } else {
            log.info("User {} has no applications – performing hard delete", id);
            user.getSkills().clear();
            resumeRepository.deleteAll(userResumes);
            userRepository.delete(user);
        }
    }

    public void hardDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ID + id));

        List<Resume> userResumes = resumeRepository.findByUserId(id);

        List<Resume> toDelete = new ArrayList<>();
        List<Resume> toKeep = new ArrayList<>();

        for (Resume resume : userResumes) {
            if (applicationRepository.existsByResumeId(resume.getId())) {
                toKeep.add(resume);
            } else {
                toDelete.add(resume);
            }
        }

        for (Resume resume : toKeep) {
            resume.setUser(null);
            resume.setStatus(ResumeStatus.USER_DELETED);
        }

        resumeRepository.deleteAll(toDelete);

        user.getSkills().clear();
        userRepository.save(user);

        userRepository.delete(user);
    }
}