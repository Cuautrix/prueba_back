package com.financial.system.core.models.dao;

import com.financial.system.core.models.dto.response.GenericCatalog;
import com.financial.system.core.models.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanksDao extends JpaRepository<Bank, Long> {
    @Query("SELECT new com.financial.system.core.models.dto.response.GenericCatalog(ct.id, ct.description) FROM Bank ct where ct.active = true")
    List<GenericCatalog> getBankActive();
}
