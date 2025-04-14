package com.financial.system.core.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OCRResponse {
    private String nombreCompleto;
    private String sexo;
    private String domicilio;
    private String clave_lector;
    private String curp;
    private String año_registro;
    private String fecha_nacimiento;
    private String seccion;
    private String vigencia;
    private String rfc;
}
