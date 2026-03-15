package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    List<Company> findByIndustry(String industry);

    List<Company> findByLocationContainingIgnoreCase(String location);

    List<Company> findByNameStartingWith(String prefix);

    boolean existsByName(String name);  // ЭТОТ МЕТОД УЖЕ ЕСТЬ, НО УБЕДИТЕСЬ, ЧТО ОН ПРИСУТСТВУЕТ

    @Query("SELECT c FROM Company c WHERE SIZE(c.vacancies) > 0")
    List<Company> findCompaniesWithVacancies();

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Company> searchByName(@Param("keyword") String keyword);

    @Query("SELECT c FROM Company c " +
            "LEFT JOIN FETCH c.vacancies " +
            "LEFT JOIN FETCH c.employers " +
            "WHERE c.id = :id")
    Optional<Company> findByIdWithJoins(@Param("id") Long id);
}