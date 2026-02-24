package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.VacancyMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public VacancyResponse findById(Long id) throws NoSuchFieldException {
        Optional<Vacancy> optionalVacancy = vacancyRepository.findById(id);
        if (optionalVacancy.isEmpty()) {
            throw new NoSuchFieldException("Vacancy not found with id: " + id);
        }
        return vacancyMapper.toResponse(optionalVacancy.get());
    }

    public List<VacancyResponse> findByJob(String job) {
        List<Vacancy> vacancies = vacancyRepository.findByJobContainingIgnoreCase(job);
        List<VacancyResponse> responses = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            responses.add(vacancyMapper.toResponse(vacancy));
        }
        return responses;
    }

    public List<VacancyResponse> findAll() {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        List<VacancyResponse> responses = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            responses.add(vacancyMapper.toResponse(vacancy));
        }
        return responses;
    }
}
