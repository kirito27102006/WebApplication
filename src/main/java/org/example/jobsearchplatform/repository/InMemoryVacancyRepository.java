package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.Vacancy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryVacancyRepository implements VacancyRepository {
    private final Map<Long, Vacancy> storage = new ConcurrentHashMap<>();


    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Vacancy save(Vacancy vacancy) {

        if (vacancy.getId() == null) {
            vacancy.setId(idGenerator.getAndIncrement());
        }

        storage.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public Optional<Vacancy> findById(Long id) {

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Vacancy> findByJobContainingIgnoreCase(String job) {
        List<Vacancy> result = new ArrayList<>();
        for (Vacancy vacancy : storage.values()) {
            if (vacancy.getJob().toLowerCase().contains(job.toLowerCase())) {
                result.add(vacancy);
            }
        }
        return result;
    }

    @Override
    public List<Vacancy> findAll() {
        return new ArrayList<>(storage.values());
    }

}
