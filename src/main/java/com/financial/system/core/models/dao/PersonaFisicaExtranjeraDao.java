package com.financial.system.core.models.dao;


import com.financial.system.core.models.entities.PersonaFisicaExtranjera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaFisicaExtranjeraDao extends JpaRepository<PersonaFisicaExtranjera, Long> {
    Optional<PersonaFisicaExtranjera> findByClientId(Long clientId);
}
