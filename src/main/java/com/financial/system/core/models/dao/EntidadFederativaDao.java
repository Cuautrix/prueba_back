package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.EntidadFederativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntidadFederativaDao extends JpaRepository<EntidadFederativa, Long> {
    Optional<EntidadFederativa> findByNumCode(Long numCode);
}
