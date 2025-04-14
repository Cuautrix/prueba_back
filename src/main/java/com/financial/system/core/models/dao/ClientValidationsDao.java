package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Client;
import com.financial.system.core.models.entities.ClientValidations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientValidationsDao extends JpaRepository<ClientValidations, Long> {

    Optional<ClientValidations> findByClient(Client client);
}
