package com.financial.system.core.models.dao;

import com.financial.system.core.models.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDAO extends JpaRepository<Client, Long> {

    @Query("select c from Client c where c.username = :username")
    Optional<Client> findByUsername(@Param("username") String username);

    @Query("select c from Client c where c.email = :email")
    Optional<Client> findByEmail(@Param("email") String email);


}
