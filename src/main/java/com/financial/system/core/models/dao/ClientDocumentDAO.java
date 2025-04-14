package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.ClientDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientDocumentDAO extends JpaRepository<ClientDocument, Long> {
    List<ClientDocument> findByClientId(Long clientId);

}

