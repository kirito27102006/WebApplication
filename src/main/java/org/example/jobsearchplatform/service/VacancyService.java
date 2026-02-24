package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.VacancyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyResponse createVacancy(VacancyCreateRequest request) {
        Vacancy vacancy = vacancyMapper.toEntity(request);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toResponse(savedVacancy);
    }

    public VacancyResponse findById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacancy not found with id: " + id));
        return vacancyMapper.toResponse(vacancy);
    }

    public List<VacancyResponse> findByJob(String job) {
        List<Vacancy> vacancies = vacancyRepository.findByJobContainingIgnoreCase(job);
        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public List<VacancyResponse> findAll() {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }
}
