package com.financial.system.core.models.dto.response.api_market_responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurpValidationResponse {
    private Data data;
    private int status;
    private String message;
    private boolean success;
    private String codigoValidacion;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String curp;
        private String sexo;
        private String nombres;
        private Historial historial;
        private int docProbatorio;
        private String paisNacimiento;
        private String apellidoMaterno;
        private String apellidoPaterno;
        private String fechaNacimiento;
        private String estadoNacimiento;
        private DatosDocProbatorio datosDocProbatorio;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Historial {
        private String estatusCurp;
        private String curpOriginal;
        private String desEstatusCURP;
        private String descripcionCurp;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatosDocProbatorio {
        private String foja;
        private String libro;
        private String anioReg;
        private String numActa;
        private String entidadRegistro;
        private String municipioRegistro;
        private String claveEntidadRegistro;
        private String claveMunicipioRegistro;
    }
}
