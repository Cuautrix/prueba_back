package com.financial.system.core.models.dao;

import com.financial.system.core.models.dto.response.GenericCatalog;
import com.financial.system.core.models.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDao extends JpaRepository<Product, Long> {
    @Query("select new com.financial.system.core.models.dto.response.GenericCatalog(p.id, p.nombre) from Product p")
    Optional<List<GenericCatalog>> getProducts();
}
