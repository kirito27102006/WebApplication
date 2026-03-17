package org.example.jobsearchplatform.service.mapper;

import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.ApplicationResponse;
import org.example.jobsearchplatform.model.Application;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    public ApplicationResponse toResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setStatus(application.getStatus().name());
        response.setCoverLetter(application.getCoverLetter());
        response.setCreatedAt(application.getCreatedAt());

        if (application.getResume() != null && application.getResume().getUser() != null) {
            response.setUserId(application.getResume().getUser().getId());
            response.setUserFullName(application.getResume().getUser()
                    .getFirstName() + " " + application.getResume().getUser().getLastName());
        }

        if (application.getVacancy() != null) {
            response.setVacancyId(application.getVacancy().getId());
            response.setVacancyTitle(application.getVacancy().getTitle());
        }

        if (application.getResume() != null) {
            response.setResumeId(application.getResume().getId());
            response.setResumeTitle(application.getResume().getTitle());
        }

        return response;
    }
}