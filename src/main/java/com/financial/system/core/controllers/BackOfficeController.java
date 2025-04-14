package com.financial.system.core.controllers;


import com.financial.system.core.services.IClientValidationsService;
import com.financial.system.core.services.IPersonaFisicaService;
import com.financial.system.shared.ResponseGenerator;
import com.financial.system.shared.constants.ApiPathConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.BACKOFFICE)
@RequiredArgsConstructor
public class BackOfficeController {
    private final IClientValidationsService clientValidationsService;
    private final IPersonaFisicaService personaFisicaService;

    @GetMapping("/getValidationsByClient")
    public ResponseEntity<?> getValidationsByClient(HttpServletRequest request) {
        var clientValidations = clientValidationsService.getClientValidations(request);

        return ResponseGenerator.generateResponse(
                "Client validations were fetched successfully", clientValidations, HttpStatus.OK, 200);
    }


    @GetMapping("/getValidationsByClientId/{clientId}")
    public ResponseEntity<?> getValidationsByClientId(@PathVariable Long clientId) {
        var clientValidations = clientValidationsService.getClientValidationsByClientId(clientId);

        return ResponseGenerator.generateResponse(
                "Client validations were fetched successfully", clientValidations, HttpStatus.OK, 200);
    }


    @GetMapping("/getAllProspects")
    public ResponseEntity<?> getAllProspects() {
        var personasFisicas = personaFisicaService.findAllProspects();

        return ResponseGenerator.generateResponse(
                "Client validations were fetched successfully", personasFisicas, HttpStatus.OK, 200);
    }
}
