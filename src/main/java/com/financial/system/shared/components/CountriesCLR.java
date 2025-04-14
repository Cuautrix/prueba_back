package com.financial.system.shared.components;

import com.financial.system.core.models.dao.CountriesDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Countries;
import com.financial.system.core.models.entities.Settings;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
@RequiredArgsConstructor
public class CountriesCLR implements CommandLineRunner {
    private final CountriesDao countriesDao;
    private final SettingsDao settingsDao;
    private final static String COUNTRIES_KEY = "EXTRACTED_COUNTRIES";


    @Override
    public void run(String... args) throws Exception {
        Settings settingsFound = settingsDao.findByKey(COUNTRIES_KEY).orElseThrow(() -> new FinancialSystemException("SEPOMEX settings not found"));

        if ("false".equals(settingsFound.getValue())) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("static/paises.txt")))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    countriesDao.save(Countries.builder().name(line).createdBy("CommandLineRunner").active(true).build());
                }
                settingsFound.setValue("true");
                settingsDao.save(settingsFound);
            } catch (IOException e) {
                log.error("Error al leer el archivo de países", e);
            }
        } else {
            log.info("Countries already loaded");
        }
    }
}
