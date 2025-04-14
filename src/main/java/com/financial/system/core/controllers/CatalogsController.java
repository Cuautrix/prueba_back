package com.financial.system.core.controllers;

import com.financial.system.core.models.dto.ClientTypeDTO;
import com.financial.system.core.models.dto.NationalityDTO;
import com.financial.system.core.models.dto.response.*;
import com.financial.system.core.models.entities.ClientType;
import com.financial.system.core.services.ICatalogService;
import com.financial.system.shared.ResponseGenerator;
import com.financial.system.shared.constants.ApiPathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.financial.system.core.models.entities.Nationality;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS)
@RequiredArgsConstructor
public class CatalogsController {

    private final ICatalogService catalogService;

    @GetMapping("/findNationalities")
    public ResponseEntity<?> getAllNationalities() {
        List<NationalityDTO> nationalities = catalogService.getAllNationalities();
        return ResponseGenerator.generateResponse(
                "Todas las nacionalidades", nationalities, HttpStatus.OK, 200);
    }

    @GetMapping("/findClientTypes")
    public ResponseEntity<?> getAllTypeClients() {
        List<ClientTypeDTO> clientTypes = catalogService.getAllTypeClients();
        return ResponseGenerator.generateResponse(
                "Todos los tipos de cliente", clientTypes, HttpStatus.OK, 200);
    }



    @GetMapping("/findAllEntidadesFederativas")
    public ResponseEntity<?> getAllEntidadesFederativas() {
        List<EntidadFederativaResponse> entidadesFederativas = catalogService.findEntidadFederativasList();
        return ResponseGenerator.generateResponse(
                "Entidades Federativas were fetched successfully", entidadesFederativas, HttpStatus.OK, 200);
    }

    @GetMapping("/findAllGenders")
    public ResponseEntity<?> getAllGenders() {
        List<GenderDto> allGenders = catalogService.findAllGenders();
        return ResponseGenerator.generateResponse(
                "Entidades Federativas were fetched successfully", allGenders, HttpStatus.OK, 200);
    }

    @GetMapping("/findAllOccupations")
    public ResponseEntity<?> getAllOccupations() {
        List<OccupationDto> occupations = catalogService.findAllOccupations();
        return ResponseGenerator.generateResponse(
                "Occupations were fetched successfully", occupations, HttpStatus.OK, 200);
    }

    @GetMapping(value = "/findBanksList")
    public ResponseEntity<?> getAllBanks() {
        List<GenericCatalog> banks = catalogService.findAllBanks();
        return ResponseGenerator.generateResponse(
                "Banks were fetched successfully", banks, HttpStatus.OK, 200);
    }

    @GetMapping(value = "/findProductTypes")
    public ResponseEntity<?> getAllProductTypes() {
        List<GenericCatalog> productTypes = catalogService.findAllProductTypes();
        return ResponseGenerator.generateResponse(
                "Product types were fetched successfully", productTypes, HttpStatus.OK, 200);
    }

    @GetMapping(value = "/findRegimens")
    public ResponseEntity<?> getAllRegimens() {
        List<RegimenResponse> regimens = catalogService.findAllRegimens();
        return ResponseGenerator.generateResponse(
                "Regimens were fetched successfully", regimens, HttpStatus.OK, 200);
    }

}
