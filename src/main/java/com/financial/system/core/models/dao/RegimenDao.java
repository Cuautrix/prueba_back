package com.financial.system.core.models.dao;

import com.financial.system.core.models.dto.response.RegimenResponse;
import com.financial.system.core.models.entities.Regimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegimenDao extends JpaRepository<Regimen, Long> {

    @Query("SELECT new com.financial.system.core.models.dto.response.RegimenResponse(r.id, r.clave, r.name) FROM Regimen r")
    Optional<List<RegimenResponse>> fetchRegimens();
}
