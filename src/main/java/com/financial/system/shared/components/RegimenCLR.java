package com.financial.system.shared.components;

import com.financial.system.core.models.dao.RegimenDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.entities.Regimen;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegimenCLR implements CommandLineRunner {
    private final RegimenDao regimenRepository;
    private final SettingsDao settingsDao;

    private static final String REGIMEN_KEY = "EXTRACTED_REGIMEN";

    @Override
    public void run(String... args) throws Exception {
        var settingsFound = settingsDao.findByKey(REGIMEN_KEY)
                .orElseThrow(() -> new FinancialSystemException("Product Types settings not found"));

        if("false".equals(settingsFound.getValue())) {
            List<Regimen> regimenes = Arrays.asList(
                    Regimen.builder().clave(605).name("Sueldos y Salarios e Ingresos Asimilados a Salarios").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(606).name("Arrendamiento").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(608).name("Demás ingresos").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(611).name("Ingresos por Dividendos (socios y accionistas)").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(612).name("Personas Físicas con Actividades Empresariales y Profesionales").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(614).name("Ingresos por intereses").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(615).name("Régimen de los ingresos por obtención de premios").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(616).name("Sin obligaciones fiscales").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(621).name("Incorporación Fiscal").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(622).name("Actividades Agrícolas, Ganaderas, Silvícolas y Pesqueras").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(629).name("De los Regímenes Fiscales Preferentes y de las Empresas Multinacionales").createdBy("CommandLineRunner").active(true).build(),
                    Regimen.builder().clave(630).name("Enajenación de acciones en bolsa de valores").createdBy("CommandLineRunner").active(true).build()
            );

            regimenRepository.saveAll(regimenes);
            settingsFound.setValue("true");
            settingsDao.save(settingsFound);
            log.info("Regimenes fiscales loaded");
        }

        if("true".equals(settingsFound.getValue())) {
            log.info("Regimenes fiscales already loaded");
        }
    }
}
