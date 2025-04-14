package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountriesDao extends JpaRepository<Countries, Long> {
    Optional<Countries> findByName(String name);
}
