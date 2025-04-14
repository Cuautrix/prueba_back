package com.financial.system.security.models.dao;

import com.financial.system.security.models.entities.ClientSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientSessionDAO extends JpaRepository<ClientSession, Long> {
}
