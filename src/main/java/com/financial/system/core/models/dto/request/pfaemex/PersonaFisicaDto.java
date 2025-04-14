package com.financial.system.core.models.dto.request.pfaemex;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaFisicaDto {
    private Long id;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String fechaNacimiento;
    private String telefono;
    private String correo;
    private Long genero;
    private Long giro;
    private Long entidadNacimiento;
    private String curp;
    private String rfc;

    private String cp;
    private int regimenId;

    public PersonaFisicaDto(String email) {
        this.correo = email;
    }
}
