package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    // Существующие методы
    List<Vacancy> findByTitleContainingIgnoreCase(String title);

    List<Vacancy> findByCompanyId(Long companyId);

    List<Vacancy> findByStatus(String status);

    List<Vacancy> findBySalaryGreaterThanEqual(Integer minSalary);

    List<Vacancy> findByRequiredExperienceLessThanEqual(Integer maxExperience);

    List<Vacancy> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT v FROM Vacancy v WHERE " +
            "(:title IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:minSalary IS NULL OR v.salary >= :minSalary) AND " +
            "(:maxExperience IS NULL OR v.requiredExperience <= :maxExperience) AND " +
            "v.status = 'ACTIVE'")
    List<Vacancy> searchVacancies(
            @Param("title") String title,
            @Param("location") String location,
            @Param("minSalary") Integer minSalary,
            @Param("maxExperience") Integer maxExperience
    );

    @Query("SELECT DISTINCT v FROM Vacancy v " +
            "LEFT JOIN FETCH v.company " +
            "LEFT JOIN FETCH v.createdBy")
    List<Vacancy> findAllWithJoins();

    @Query("SELECT v FROM Vacancy v " +
            "LEFT JOIN FETCH v.company " +
            "LEFT JOIN FETCH v.createdBy " +
            "WHERE v.id = :id")
    Optional<Vacancy> findByIdWithJoins(@Param("id") Long id);

    @Query("SELECT DISTINCT v FROM Vacancy v " +
            "LEFT JOIN FETCH v.company " +
            "LEFT JOIN FETCH v.createdBy " +
            "WHERE (:title IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:minSalary IS NULL OR v.salary >= :minSalary) AND " +
            "(:maxExperience IS NULL OR v.requiredExperience <= :maxExperience)")
    List<Vacancy> searchVacanciesWithJoins(
            @Param("title") String title,
            @Param("location") String location,
            @Param("minSalary") Integer minSalary,
            @Param("maxExperience") Integer maxExperience
    );

    @Query("SELECT DISTINCT v FROM Vacancy v " +
            "LEFT JOIN FETCH v.company " +
            "LEFT JOIN FETCH v.createdBy " +
            "WHERE v.company.id = :companyId")
    List<Vacancy> findByCompanyIdWithJoins(@Param("companyId") Long companyId);
}