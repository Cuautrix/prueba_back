package com.financial.system.shared.components;

import com.financial.system.core.models.dao.BanksDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Bank;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCLR implements CommandLineRunner {
    private final BanksDao bancoDao;
    private final SettingsDao settingsDao;
    private static final String BANKS_KEY = "EXTRACTED_BANKS";
    private static final String BANKS_FILE_PATH = "static/banks.txt";

    @Override
    public void run(String... args) throws Exception {
        var settingsFound = settingsDao.findByKey(BANKS_KEY)
                .orElseThrow(() -> new FinancialSystemException("Bank settings not found"));

        if ("false".equals(settingsFound.getValue())) {
            cargarBancosDesdeArchivo();
            settingsFound.setValue("true");
            settingsDao.save(settingsFound);
            log.info("Banks loaded");
        }

        if ("true".equals(settingsFound.getValue())) {
            log.info("Banks already loaded");
        }
    }

    private void cargarBancosDesdeArchivo() throws IOException {
        log.error("Cargando bancos desde archivo");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(BANKS_FILE_PATH), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] partes = line.split("\\|");
                if (partes.length == 3) {
                    String nombre = partes[0].trim();
                    String clave = partes[1].trim();
                    String razonSocial = partes[2].trim();

                    Bank banco = Bank.builder()
                            .description(nombre)
                            .clave(clave)
                            .razonSocial(razonSocial)
                            .createdBy("CommandLineRunner")
                            .active(true)
                            .build();

                    bancoDao.save(banco);
                }
            }
        }
    }
}
