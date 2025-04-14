package com.financial.system.shared.components;

import com.financial.system.core.models.dao.EntidadFederativaDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.EntidadFederativa;
import com.financial.system.core.models.entities.Settings;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EstadoCLR implements CommandLineRunner {
    private final SettingsDao settingsDao;
    private final EntidadFederativaDao entidadFederativaDao;

    private static final String STATES_KEY = "EXTRACTED_STATES";
    private static final int BATCH_SIZE = 500;

    @Override
    @Transactional
    public void run(String... args) throws FinancialSystemException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("static/entidades_federativas.txt"), StandardCharsets.UTF_8))) {

            String line;
            Settings settingsFound = settingsDao.findByKey(STATES_KEY).orElseThrow(() -> new FinancialSystemException("States settings not found"));

            if (settingsFound.getValue().equals("false")) {
                List<EntidadFederativa> statesBatch = new ArrayList<>(BATCH_SIZE);

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("\\|");

                    EntidadFederativa entidadFederativa = EntidadFederativa.builder()
                            .name(data.length > 0 && !data[0].isEmpty() ? data[0] : null)
                            .code(data.length > 0 && !data[1].isEmpty() ? data[1] : null)
                            .numCode(data.length > 0 && !data[2].isEmpty() ? Long.parseLong(data[2]) : null)
                            .createdBy("CommandLineRunner")
                            .build();

                    statesBatch.add(entidadFederativa);

                    if (statesBatch.size() >= BATCH_SIZE) {
                        entidadFederativaDao.saveAll(statesBatch);
                        log.info(statesBatch.size() + " records saved");
                        statesBatch.clear();
                    }
                }
                if (!statesBatch.isEmpty()) {
                    entidadFederativaDao.saveAll(statesBatch);
                    log.info("saved " + statesBatch.size() + " records remaining");
                }
            }
            if (settingsFound.getValue().equals("false")) {
                settingsFound.setValue("true");
            }
            if(settingsFound.getValue().equals("true")) {
                log.info("States data already loaded");
            }
        } catch (Exception e) {
            log.error("Error to process states txt file", e);
        }
    }
}
