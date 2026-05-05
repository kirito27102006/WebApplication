package org.example.jobsearchplatform.repository;

import org.example.jobsearchplatform.model.AuthAccount;
import org.example.jobsearchplatform.model.enums.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

    boolean existsByLogin(String login);

    Optional<AuthAccount> findByLogin(String login);

    Optional<AuthAccount> findByLoginAndRole(String login, AccountRole role);
}
