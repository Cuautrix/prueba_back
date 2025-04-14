package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalityDAO extends JpaRepository<Nationality, Long> {
}
