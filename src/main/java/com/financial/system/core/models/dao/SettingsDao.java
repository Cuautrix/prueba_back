package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsDao extends JpaRepository<Settings, Long> {
    Optional<Settings> findByKey(String key);
}
