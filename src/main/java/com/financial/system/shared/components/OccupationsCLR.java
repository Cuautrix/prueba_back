package com.financial.system.shared.components;

import com.financial.system.core.models.dao.OccupationDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Occupation;
import com.financial.system.core.models.entities.Settings;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OccupationsCLR implements CommandLineRunner {
    private final OccupationDao occupationDao;
    private final SettingsDao settingsDao;
    private static final String OCCUPATIONS_KEY = "OCCUPATIONS_EXTRACTED";

    @Override
    public void run(String... args) throws Exception {
        String[] ocupaciones = {
                "Casino",
                "Abogado",
                "Comerciante",
                "Licenciado",
                "Doctor",
                "Ingeniero",
                "Arquitecto",
                "Enfermero",
                "Profesor",
                "Psicólogo",
                "Contador",
                "Dentista",
                "Farmacéutico",
                "Diseñador",
                "Chef",
                "Veterinario",
                "Electricista",
                "Carpintero",
                "Programador",
                "Médico",
                "Periodista"
        };


        Settings settingsFound = settingsDao.findByKey(OCCUPATIONS_KEY).orElseThrow(() -> new FinancialSystemException("States settings not found"));


        if("false".equals(settingsFound.getValue())){
            for (String ocupacion : ocupaciones) {
                Occupation occupation = Occupation.builder()
                        .description(ocupacion)
                        .createdBy("CommandLineRunner")
                        .active(true)
                        .build();

                occupationDao.save(occupation);

            }
            settingsFound.setValue("true");
            settingsDao.save(settingsFound);
            log.info("Occupations loaded");
        }

        if("true".equals(settingsFound.getValue())){
            log.info("Occupations already loaded");
        }
    }
}
