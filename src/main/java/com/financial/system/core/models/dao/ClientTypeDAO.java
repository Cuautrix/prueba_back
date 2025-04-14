package com.financial.system.core.models.dao;


import com.financial.system.core.models.entities.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTypeDAO extends JpaRepository<ClientType, Long> {
}
