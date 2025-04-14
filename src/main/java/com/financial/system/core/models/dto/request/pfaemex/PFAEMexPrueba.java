package com.financial.system.core.models.dto.request.pfaemex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PFAEMexPrueba {
    private Long id;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String rfc;
}
