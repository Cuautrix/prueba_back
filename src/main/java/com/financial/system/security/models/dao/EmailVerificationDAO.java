package com.financial.system.security.models.dao;

import com.financial.system.security.models.entities.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationDAO extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByVerificationTokenAndClientId(String code, Long id);
}
