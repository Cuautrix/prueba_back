package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.PersonaFisicaMex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaFisicaDao extends JpaRepository<PersonaFisicaMex, Long> {
    Optional<PersonaFisicaMex> findByClientId(Long clientId);
    List<PersonaFisicaMex> findByCurpAndClientIdNot(String curp, Long id);
    List<PersonaFisicaMex> findByRfcAndClientIdNot(String curp, Long id);
    List<PersonaFisicaMex> findByPhoneAndClientIdNot(String curp, Long id);


}
