package com.financial.system.core.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ZipCodeAddress {
    private String cp;
    private List<Colonia> colonias;
    private String municipio;
    private String estado;
    private Long codigoEstado;
    private String ciudad;
    private String pais;
    private List<String> idSuburb;
}
