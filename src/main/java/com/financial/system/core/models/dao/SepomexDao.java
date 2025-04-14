package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Sepomex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SepomexDao extends JpaRepository<Sepomex, Long> {
    Optional<List<Sepomex>> findByCodigoPostal(Long zipCode);
}
