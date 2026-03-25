package org.example.jobsearchplatform.service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.example.jobsearchplatform.model.Application;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    public ApplicationResponse toResponse(Application application) {
        if (application == null) {
            return null;
        }

        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());

        if (application.getStatus() != null) {
            response.setStatus(application.getStatus().name());
        }

        response.setCoverLetter(application.getCoverLetter());
        response.setCreatedAt(application.getCreatedAt());

        // Безопасное получение данных пользователя
        if (application.getResume() != null) {
            response.setResumeId(application.getResume().getId());
            response.setResumeTitle(application.getResume().getTitle());

            if (application.getResume().getUser() != null) {
                response.setUserId(application.getResume().getUser().getId());
                response.setUserFullName(
                        application.getResume().getUser().getFirstName() + " " +
                                application.getResume().getUser().getLastName()
                );
            }
        }


        if (application.getVacancy() != null) {
            response.setVacancyId(application.getVacancy().getId());
            response.setVacancyTitle(application.getVacancy().getTitle());
        }

        return response;
    }
}