package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.enums.ResumeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user WHERE r.user.id = :userId")
    List<Resume> findByUserIdWithUser(@Param("userId") Long userId);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user WHERE r.id = :id")
    Optional<Resume> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user")
    List<Resume> findAllWithUser();

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user WHERE " +
            "LOWER(r.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Resume> findBySkillWithUser(@Param("skill") String skill);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user WHERE " +
            "LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Resume> findByLocationWithUser(@Param("location") String location);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user WHERE " +
            "r.expectedSalary <= :maxSalary")
    List<Resume> findByMaxSalaryWithUser(@Param("maxSalary") Integer maxSalary);

    List<Resume> findByUserId(Long userId);

    List<Resume> findByStatus(ResumeStatus status);

    List<Resume> findByLocationContainingIgnoreCase(String location);

    List<Resume> findByExpectedSalaryLessThanEqual(Integer maxSalary);

    @Query("SELECT r FROM Resume r WHERE " +
            "LOWER(r.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Resume> findBySkill(@Param("skill") String skill);
}