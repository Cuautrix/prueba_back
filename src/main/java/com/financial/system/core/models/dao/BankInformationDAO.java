package com.financial.system.core.models.dao;


import com.financial.system.core.models.dto.request.BankInformation;
import com.financial.system.core.models.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankInformationDAO extends JpaRepository<BankInformation, Long> {
    Optional<BankInformation> findByClientId(Long clientId);
    List<BankInformation> findAllByClientId(Long clientId);

}


