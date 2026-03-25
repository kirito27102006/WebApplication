package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Application;
import org.example.jobsearchplatform.model.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query(value = "SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.resume r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH a.vacancy",
            countQuery = "SELECT COUNT(a) FROM Application a")
    Page<Application> findAllWithJoins(Pageable pageable);

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

    @Query("SELECT DISTINCT a FROM Application a " +
            "JOIN FETCH a.resume r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH a.vacancy v " +
            "WHERE (:userId IS NULL OR u.id = :userId) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:vacancyTitle = '' OR LOWER(v.title) LIKE LOWER(CONCAT('%', :vacancyTitle, '%'))) " +
            "AND (:resumeTitle = '' OR LOWER(r.title) LIKE LOWER(CONCAT('%', :resumeTitle, '%')))")
    List<Application> searchByFiltersJpql(@Param("userId") Long userId,
                                          @Param("status") ApplicationStatus status,
                                          @Param("vacancyTitle") String vacancyTitle,
                                          @Param("resumeTitle") String resumeTitle);

    @Query(value = "SELECT a.id AS id, " +
            "u.id AS userId, " +
            "CONCAT(u.first_name, ' ', u.last_name) AS userFullName, " +
            "v.id AS vacancyId, " +
            "v.title AS vacancyTitle, " +
            "r.id AS resumeId, " +
            "r.title AS resumeTitle, " +
            "a.cover_letter AS coverLetter, " +
            "a.status AS status, " +
            "a.created_at AS createdAt " +
            "FROM applications a " +
            "JOIN resumes r ON r.id = a.resume_id " +
            "JOIN users u ON u.id = r.user_id " +
            "JOIN vacancies v ON v.id = a.vacancy_id " +
            "WHERE (:userId IS NULL OR u.id = :userId) " +
            "AND (:status IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
            "AND (:vacancyTitle IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :vacancyTitle, '%'))) " +
            "AND (:resumeTitle IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :resumeTitle, '%')))",
            nativeQuery = true)
    List<ApplicationNativeSearchView> searchByFiltersNative(@Param("userId") Long userId,
                                                            @Param("status") String status,
                                                            @Param("vacancyTitle") String vacancyTitle,
                                                            @Param("resumeTitle") String resumeTitle);

    boolean existsByVacancyId(Long vacancyId);

    boolean existsByResumeId(Long resumeId);
}
