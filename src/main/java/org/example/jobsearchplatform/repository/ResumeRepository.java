package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Resume;
import org.example.jobsearchplatform.model.enums.ResumeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUserId(Long userId);

    List<Resume> findByStatus(ResumeStatus status);

    List<Resume> findByLocationContainingIgnoreCase(String location);

    List<Resume> findByExpectedSalaryLessThanEqual(Integer maxSalary);

    @Query("SELECT r FROM Resume r WHERE " +
            "LOWER(r.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Resume> findBySkill(@Param("skill") String skill);
}