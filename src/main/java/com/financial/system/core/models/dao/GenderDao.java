package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderDao extends JpaRepository<Gender, Long> {
}
