package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.User;
import org.example.jobsearchplatform.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE SIZE(u.resumes) > 0")
    List<User> findUsersWithResumes();

    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.resumes " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithResumes(@Param("id") Long id);
}