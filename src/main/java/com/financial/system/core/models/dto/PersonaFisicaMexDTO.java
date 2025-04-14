package com.financial.system.core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaFisicaMexDTO {
    private LocalDate birthDate;
    private String phone;
    private Long clientId;
    private Long paisOrigen;

    private String name;
    private String lastName;
    private String secondLastName;

    private String codigoPostal;
    private String colonia;
    private String municipio;
    private String ciudad;
    private String calle;
    private String numeroExterior;
    private String numeroInterior;

    private String curp;
    private String rfc;
    private String idsuburb;
}
