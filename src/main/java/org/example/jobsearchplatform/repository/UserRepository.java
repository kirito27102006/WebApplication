package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.User;
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

    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") String status);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.resumes " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithResumes(@Param("id") Long id);

    // УДАЛЁН МЕТОД findByIdWithApplications - его больше нет!
}