package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.ClientValidationStatus;
import com.financial.system.core.models.enums.ClientStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientValidationStatusDAO extends JpaRepository<ClientValidationStatus,Long> {

    Optional<ClientValidationStatus> getClientValidationStatusByDescription(ClientStatusEnum description);
}
