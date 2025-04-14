package com.financial.system.security.models.dao;

import com.financial.system.core.models.entities.Client;
import com.financial.system.security.models.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenDAO extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndClient(String token, Client client);

    Optional<PasswordResetToken> findByToken(String token);
}
