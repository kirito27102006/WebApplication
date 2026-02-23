package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Vacancy;

import java.util.List;
import java.util.Optional;

public interface VacancyRepository {
    Vacancy save(Vacancy vacancy);

    Optional<Vacancy> findById(Long id);

    List<Vacancy> findAll();

    void deleteById(Long id);
}
