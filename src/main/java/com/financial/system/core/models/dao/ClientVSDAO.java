package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.ClientVS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository

public interface ClientVSDAO extends JpaRepository<ClientVS, Long> {
    Optional<ClientVS> findByClientId(Long clientId);

}
