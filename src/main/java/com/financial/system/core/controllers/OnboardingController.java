package com.financial.system.core.controllers;


import com.financial.system.core.models.dto.ClientFullInfoDTO;
import com.financial.system.core.models.dto.ClientVSrequest;
import com.financial.system.core.models.dto.UploadDocumentRequest;
import com.financial.system.core.models.dto.request.ClientBankInformation;
import com.financial.system.core.models.dto.request.OCRDto;
import com.financial.system.core.models.dto.request.pfaemex.PFAEMexDireccionProspectoRequest;
import com.financial.system.core.models.dto.request.pfaemex.PersonaFisicaDto;

import com.financial.system.core.models.dto.request.pfaemex.UploadDocumentsRequest;
import com.financial.system.core.models.dto.response.ZipCodeAddress;
import com.financial.system.core.models.entities.ClientVS;
import com.financial.system.core.services.IPersonaFisicaService;
import com.financial.system.core.services.IZipCodeAddressService;
import com.financial.system.shared.ResponseGenerator;
import com.financial.system.shared.constants.ApiPathConstants;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.shared.payload.FinancialSystemResponse;
import com.financial.system.vendors.IAPIMarketService;
import com.financial.system.vendors.IOCRService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING)
@RequiredArgsConstructor
public class OnboardingController {
    private final IPersonaFisicaService personaFisicaService;
    private final IZipCodeAddressService zipCodeAddressService;
    private final IOCRService ocrService;
    private final IAPIMarketService apiMarketService;

    @PostMapping(value = "/savePFAEDatosProspecto")
    public ResponseEntity<?> savePFAEDatosProspecto(HttpServletRequest servletRequest, @Valid @RequestBody PersonaFisicaDto request) throws FinancialSystemException {
        return ResponseEntity.ok(personaFisicaService.saveDatosProspecto(servletRequest, request));
    }



    @PostMapping(value = "/ocr")
    public ResponseEntity<FinancialSystemResponse> getDataWithOCR(@RequestBody OCRDto request,HttpServletRequest httpServletRequest) throws FinancialSystemException {
        return ResponseGenerator.generateResponse("Data extracted successfully", ocrService.extractData(request, httpServletRequest), org.springframework.http.HttpStatus.OK, 200);
    }



    @PostMapping(value = "/contribuidorOcr")
    public ResponseEntity<FinancialSystemResponse> getDataWithINE(@RequestBody OCRDto request) throws FinancialSystemException {
        return ResponseGenerator.generateResponse("Data extracted successfully", ocrService.extractIneData(request), org.springframework.http.HttpStatus.OK, 200);
    }

    @GetMapping("/getPFAEMexDatosProspectoInformation")
    public ResponseEntity<PersonaFisicaDto> getPFAEMexDatosProspectoInformation(HttpServletRequest servletRequest) {
        return ResponseEntity.ok(personaFisicaService.findPersonaFisicaInformation(servletRequest));
    }



    @PostMapping(value = "/savePFAEDireccionProspecto")
    public ResponseEntity<?> savePFAEDireccionProspecto(HttpServletRequest servletRequest, @Valid @RequestBody PFAEMexDireccionProspectoRequest request) throws FinancialSystemException {
        return ResponseEntity.ok(personaFisicaService.saveDireccionProspecto(servletRequest, request));
    }

    @GetMapping("/findAddressByZipCode/{zipCode}")
    public ResponseEntity<ZipCodeAddress> findAddressByZipCode(@PathVariable("zipCode") String zipCode) throws FinancialSystemException {
        return ResponseEntity.ok(zipCodeAddressService.obtainZipCodeInformation(zipCode));
    }

//    @PostMapping(value = "/savePFAEMexDireccionProspecto")
//    public ResponseEntity<?> savePFAEMexDireccionProspecto(HttpServletRequest servletRequest, @Valid @RequestBody PFAEMexDireccionProspectoRequest request) throws FinancialSystemException, MonekiAurumException {
//        return ResponseEntity.ok(personaFisicaService.saveDireccionProspecto(servletRequest, request));
//    }

    @PostMapping(value = "/saveDatosBancariosProspecto")
    public ResponseEntity<FinancialSystemResponse> saveDatosBancariosProspecto(
            HttpServletRequest servletRequest,
            @Valid @RequestBody ClientBankInformation request
    ) throws FinancialSystemException {
        return ResponseEntity.ok(personaFisicaService.saveClientBankInformation(servletRequest, request));
    }

    @GetMapping("/getClientFullInfo/{id}")
    public ResponseEntity<ClientFullInfoDTO> getClientFullInfo(@PathVariable Long id) {
        return ResponseEntity.ok(personaFisicaService.findClientFullInfoById(id));
    }



    @PostMapping("/uploadDocuments")
    public ResponseEntity<?> uploadDocuments(
            HttpServletRequest request,
            @Valid @RequestBody List<UploadDocumentRequest> documents
    ) throws FinancialSystemException {
        return ResponseEntity.ok(personaFisicaService.uploadDocuments(request, documents));
    }

    @PutMapping("/update-status")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestBody ClientVSrequest request) {
        personaFisicaService.updateValidationStatus(request);
        return ResponseEntity.ok(Map.of("message", "Estado de validación actualizado correctamente"));
    }





//    @PostMapping("/renapo/valida-curp")
//    public ResponseEntity<?> validaCurp(@RequestParam String curp) {
//        var response = apiMarketService.validateCurp(curp);
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping("/sat/grupo/obtener-rfc")
//    public ResponseEntity<?> obtenerRfc(@RequestParam String curp, HttpServletRequest servletRequest) {
//        var response = apiMarketService.getRfcData(curp, servletRequest);
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping("/sat/grupo/validar-datos")
//    public ResponseEntity<?> validarDatos(
//            @RequestParam String nombre,
//            @RequestParam String rfc,
//            @RequestParam String regimen,
//            @RequestParam String cp) {
//        var response = apiMarketService.validateRfc(rfc, nombre, Integer.parseInt(regimen), cp);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/sat/v2/lista69b")
//    public ResponseEntity<?> lista69b(@RequestParam String rfc) {
//        var response = apiMarketService.list69b(rfc);
//        return ResponseEntity.ok(response);
//    }
}
