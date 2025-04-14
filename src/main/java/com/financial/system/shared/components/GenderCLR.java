package com.financial.system.shared.components;

import com.financial.system.core.models.dao.GenderDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Gender;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenderCLR implements CommandLineRunner {
    private final GenderDao genderDao;
    private final SettingsDao settingsDao;
    private static final String GENDERS_KEY = "EXTRACTED_GENDERS";

    @Override
    public void run(String... args) throws Exception {
        String[][] genders = {
                {"Masculino", "H"},
                {"Femenino", "M"}
        };

        var settingsFound = settingsDao.findByKey(GENDERS_KEY)
                .orElseThrow(() -> new FinancialSystemException("Gender settings not found"));

        if ("false".equals(settingsFound.getValue())) {
            for (String[] genderData : genders) {
                String description = genderData[0];
                String code = genderData[1];

                Gender gender = Gender.builder()
                        .description(description)
                        .code(code)
                        .createdBy("CommandLineRunner")
                        .active(true)
                        .build();

                genderDao.save(gender);
            }

            settingsFound.setValue("true");
            settingsDao.save(settingsFound);
            log.info("Genders loaded");
        }

        if ("true".equals(settingsFound.getValue())) {
            log.info("Genders already loaded");
        }
    }
}
