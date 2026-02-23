package org.example.jobsearchplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.VacancyCreateRequest;
import org.example.jobsearchplatform.dto.VacancyResponse;
import org.example.jobsearchplatform.model.Vacancy;
import org.example.jobsearchplatform.repository.VacancyRepository;
import org.example.jobsearchplatform.service.mapper.VacancyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service  // Говорит Spring, что это сервис (бизнес-логика)
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;  // Внедряем зависимость
    private final VacancyMapper vacancyMapper;          // Внедряем зависимость

    // Создание новой машины
    public VacancyResponse createVacancy(VacancyCreateRequest request) {

        // 2. Преобразуем DTO в сущность
        Vacancy vacancy = vacancyMapper.toEntity(request);

        // 3. Сохраняем в репозитории
        Vacancy savedVacancy = vacancyRepository.save(vacancy);

        // 4. Преобразуем обратно в DTO и возвращаем
        return vacancyMapper.toResponse(savedVacancy);
    }

    // Получение машины по ID
    public VacancyResponse findById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacancy not found with id: " + id));
        return vacancyMapper.toResponse(vacancy);
    }

    // Получение всех машин (с возможностью фильтрации по статусу)
    public List<VacancyResponse> findAll() {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        // Если указан статус - фильт
        // Преобразуем все машины в DTO
        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    // Удаление машины
    public void deleteVacancy(Long id) throws NoSuchFieldException {
        if (!vacancyRepository.findById(id).isPresent()) {
            throw new NoSuchFieldException("Vacancy not found with id: " + id);
        }
        vacancyRepository.deleteById(id);
    }
}
