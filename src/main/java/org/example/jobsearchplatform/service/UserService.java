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

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        return userMapper.toResponse(getUserById(id));
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
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
        User user = getUserById(id);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    public void blockUser(Long id) {
        getUserById(id).setStatus(UserStatus.BLOCKED);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        List<Resume> userResumes = resumeRepository.findByUserId(id);

        boolean hasApplications = userResumes.stream().anyMatch(hasApplications());

        if (hasApplications) {
            log.info("User {} has applications - performing soft delete", id);
            user.setStatus(UserStatus.DELETED);
            userResumes.forEach(resume -> resume.setStatus(ResumeStatus.USER_DELETED));
        } else {
            log.info("User {} has no applications - performing hard delete", id);
            user.getSkills().clear();
            resumeRepository.deleteAll(userResumes);
            userRepository.delete(user);
        }
    }

    public void hardDeleteUser(Long id) {
        User user = getUserById(id);
        List<Resume> userResumes = resumeRepository.findByUserId(id);

        Map<Boolean, List<Resume>> resumesByApplications = userResumes.stream()
                .collect(Collectors.partitioningBy(hasApplications()));

        resumesByApplications.getOrDefault(true, List.of()).forEach(resume -> {
            resume.setUser(null);
            resume.setStatus(ResumeStatus.USER_DELETED);
        });

        resumeRepository.deleteAll(resumesByApplications.getOrDefault(false, List.of()));

        user.getSkills().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_ID + id));
    }

    private Predicate<Resume> hasApplications() {
        return resume -> applicationRepository.existsByResumeId(resume.getId());
    }
}
