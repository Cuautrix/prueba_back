package com.financial.system.core.services.impl;

import com.financial.system.core.models.dao.SepomexDao;
import com.financial.system.core.models.dto.response.Colonia;
import com.financial.system.core.models.dto.response.ZipCodeAddress;
import com.financial.system.core.models.entities.Sepomex;
import com.financial.system.core.services.IZipCodeAddressService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZipCodeAddressService implements IZipCodeAddressService {
    private final SepomexDao sepomexDao;

    @Override
    public ZipCodeAddress obtainZipCodeInformation(String zipCode) throws FinancialSystemException {
        if (zipCode == null || zipCode.isEmpty()) {
            throw new FinancialSystemException("El codigo postal no debe ser nulo");
        }

        if (!zipCode.matches("\\d+")) {
            throw new FinancialSystemException("El codigo postal debe ser numerico");
        }

        List<Sepomex> codigosPostalesList = this.sepomexDao.findByCodigoPostal(Long.parseLong(zipCode)).orElseThrow(() -> new FinancialSystemException("El codigo postal ingresado no es valido"));

        if (codigosPostalesList.isEmpty()) {
            throw new FinancialSystemException("El codigo postal ingresado no es valido");
        }

        Sepomex codigoPostal = codigosPostalesList.get(0);

        List<Colonia> colonias = codigosPostalesList.stream().map(sepomex -> Colonia.builder()
                .name(sepomex.getColonia())
                .countryName("México")
                .build()).toList();


        ZipCodeAddress cleanAddress = ZipCodeAddress.builder()
                .cp(codigoPostal.getCodigoPostal().toString())
                .ciudad(codigoPostal.getCiudad())
                .estado(codigoPostal.getEstado())
                .municipio(codigoPostal.getMunicipio())
                .pais("México")
                .colonias(colonias)
                .build();

        return cleanAddress;
    }
}
