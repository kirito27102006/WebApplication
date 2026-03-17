package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT a FROM Application a WHERE a.resume.user.id = :userId")
    List<Application> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.resume r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH a.vacancy " +
            "WHERE r.user.id = :userId")
    List<Application> findByUserIdWithJoins(@Param("userId") Long userId);

    List<Application> findByVacancyId(Long vacancyId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.resume r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH a.vacancy " +
            "WHERE a.vacancy.id = :vacancyId")
    List<Application> findByVacancyIdWithJoins(@Param("vacancyId") Long vacancyId);

    @Query("SELECT COUNT(a) > 0 FROM Application a " +
            "WHERE a.resume.user.id = :userId AND a.vacancy.id = :vacancyId")
    boolean existsByUserIdAndVacancyId(@Param("userId") Long userId, @Param("vacancyId") Long vacancyId);

    boolean existsByVacancyId(Long vacancyId);

    boolean existsByResumeId(Long resumeId);
}