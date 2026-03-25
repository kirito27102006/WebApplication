package org.example.jobsearchplatform.repository;

import java.time.LocalDateTime;

public interface ApplicationNativeSearchView {

    Long getId();

    Long getUserId();

    String getUserFullName();

    Long getVacancyId();

    String getVacancyTitle();

    Long getResumeId();

    String getResumeTitle();

    String getCoverLetter();

    String getStatus();

    LocalDateTime getCreatedAt();
}
