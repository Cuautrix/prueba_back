package com.financial.system.shared.components;

import com.financial.system.core.models.dao.SepomexDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Sepomex;
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
public class SepomexCLR implements CommandLineRunner {
    private final SettingsDao settingsDao;
    private final SepomexDao sepomexDao;

    private static final String SEPOMEX_KEY = "EXTRACTED_SEPOMEX";
    private static final int BATCH_SIZE = 500;

    @Override
    @Transactional
    public void run(String... args) throws FinancialSystemException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("static/codigos_postales.txt"), StandardCharsets.UTF_8))) {

            String line;
            Settings settingsFound = settingsDao.findByKey(SEPOMEX_KEY).orElseThrow(() -> new FinancialSystemException("SEPOMEX settings not found"));

            if (settingsFound.getValue().equals("false")) {
                List<Sepomex> sepomexBatch = new ArrayList<>(BATCH_SIZE);

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("\\|");
                    Sepomex sepomex = Sepomex.builder()
                            .codigoPostal(data.length > 0 && !data[0].isEmpty() ? Long.parseLong(data[0]) : null)
                            .colonia(data.length > 1 && !data[1].isEmpty() ? new String(data[1].getBytes("ISO-8859-1"), StandardCharsets.UTF_8) : null)
                            .tipoAsentamiento(data.length > 2 && !data[2].isEmpty() ? new String(data[2].getBytes("ISO-8859-1"), StandardCharsets.UTF_8) : null)
                            .municipio(data.length > 3 && !data[3].isEmpty() ? data[3] : null)
                            .estado(data.length > 4 && !data[4].isEmpty() ? data[4] : null)
                            .ciudad(data.length > 5 && !data[5].isEmpty() ? new String(data[5].getBytes("ISO-8859-1"), StandardCharsets.UTF_8) : null)
                            .zona(data.length > 13 && !data[13].isEmpty() ? data[13] : null)
                            .createdBy("CommandLineRunner")
                            .active(true)
                            .build();

                    sepomexBatch.add(sepomex);

                    if (sepomexBatch.size() >= BATCH_SIZE) {
                        sepomexDao.saveAll(sepomexBatch);
                        log.info(sepomexBatch.size() + " records saved");
                        sepomexBatch.clear();
                    }
                }
                if (!sepomexBatch.isEmpty()) {
                    sepomexDao.saveAll(sepomexBatch);
                    log.info("saved " + sepomexBatch.size() + " records remaining");
                }
            }
            if (settingsFound.getValue().equals("false")) {
                settingsFound.setValue("true");
            }
            if(settingsFound.getValue().equals("true")) {
                log.info("Sepomex data already loaded");
            }
        } catch (Exception e) {
            log.error("Error to process sepomex txt file", e);
        }
    }
}
