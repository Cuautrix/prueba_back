package com.financial.system.core.models.dao;

import com.financial.system.core.models.dto.response.GenericCatalog;
import com.financial.system.core.models.entities.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupationDao extends JpaRepository<Occupation, Long> {
    @Query("SELECT new com.financial.system.core.models.dto.response.GenericCatalog(ct.id, ct.description) FROM Occupation ct where ct.active = true")
    List<GenericCatalog> getOccupationActive();
}
