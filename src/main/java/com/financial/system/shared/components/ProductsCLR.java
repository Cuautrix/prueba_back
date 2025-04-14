package com.financial.system.shared.components;


import com.financial.system.core.models.dao.ProductDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Product;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductsCLR implements CommandLineRunner {
    private final SettingsDao settingsDao;
    private final ProductDao productDao;
    private static final String PRODUCTS_KEY = "EXTRACTED_PRODUCTS";

    @Override
    public void run(String... args) throws Exception {
        var settingsFound = settingsDao.findByKey(PRODUCTS_KEY)
                .orElseThrow(() -> new FinancialSystemException("Product Types settings not found"));

        if ("false".equals(settingsFound.getValue())) {
            loadProducts();
            settingsFound.setValue("true");
            settingsDao.save(settingsFound);
            log.info("Products loaded");
        }

        if ("true".equals(settingsFound.getValue())) {
            log.info("Products already loaded");
        }
    }

    private void loadProducts() {
        String[] products = {"Seguros de Auto", "Seguros de Hogar", "Seguros de Vida", "Seguros de Gastos Médicos"};

        for (String product : products) {
            Product productToSave = Product.builder()
                    .nombre(product)
                    .createdBy("CommandLineRunner")
                    .build();
            productDao.save(productToSave);
        }

    }

}
