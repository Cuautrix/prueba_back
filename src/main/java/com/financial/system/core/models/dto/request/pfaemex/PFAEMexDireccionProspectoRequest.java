package com.financial.system.core.models.dto.request.pfaemex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PFAEMexDireccionProspectoRequest {
    private Long paisDomicilio;
    private Long entidadFederativa;
    private String cp;
    private String ciudad;
    private String municipio;
    private String colonia;
    private String calle;
    private String numero;
    private String interior;
    private String idSuburb;
}
