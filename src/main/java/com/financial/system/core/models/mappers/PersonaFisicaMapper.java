package com.financial.system.core.models.mappers;

import com.financial.system.core.models.dto.request.pfaemex.PersonaFisicaDto;
import com.financial.system.core.models.entities.PersonaFisicaExtranjera;
import com.financial.system.core.models.entities.PersonaFisicaMex;

public class PersonaFisicaMapper {
    public static PersonaFisicaDto toResponse(PersonaFisicaMex client, String email) {
        if (client == null) {
            return null;
        }

        PersonaFisicaDto response = new PersonaFisicaDto();
        response.setId(client.getId());
        response.setGenero(client.getGender().getId());
        response.setGiro(client.getOccupation().getId());
        response.setEntidadNacimiento(client.getEntidadFederativa().getId());
        response.setNombre(client.getName());
        response.setPrimerApellido(client.getLastName());
        response.setSegundoApellido(client.getSecondLastName());
        response.setFechaNacimiento(client.getBirthDate().toString());
        response.setTelefono(client.getPhone());
        response.setCurp(client.getCurp());
        response.setCorreo(email);
        response.setRfc(client.getRfc());

        return response;
    }

    public static PersonaFisicaDto toResponse(PersonaFisicaExtranjera client, String email) {
        if (client == null) {
            return null;
        }

        PersonaFisicaDto response = new PersonaFisicaDto();
        response.setId(client.getId());
        response.setGenero(client.getGender().getId());
        response.setGiro(client.getOccupation().getId());
        response.setEntidadNacimiento(client.getEntidadFederativaId());
        response.setNombre(client.getName());
        response.setCorreo(email);
        response.setPrimerApellido(client.getLastName());
        response.setSegundoApellido(client.getSecondLastName());
        response.setFechaNacimiento(client.getBirthDate().toString());
        response.setTelefono(client.getPhone());
        response.setCurp(client.getCurp());
        response.setRfc(client.getRfc());

        return response;
    }
}