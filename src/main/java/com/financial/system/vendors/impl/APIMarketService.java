package com.financial.system.vendors.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.system.core.models.dao.ClientValidationsDao;
import com.financial.system.core.models.dao.SettingsDao;
import com.financial.system.core.models.dto.response.api_market_responses.CurpValidationResponse;
import com.financial.system.core.models.dto.response.api_market_responses.List69B;
import com.financial.system.core.models.dto.response.api_market_responses.RfcNotFoundResponse;
import com.financial.system.core.models.dto.response.api_market_responses.RfcResponse;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.models.entities.ClientValidations;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.vendors.IAPIMarketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIMarketService implements IAPIMarketService {
    @Value("${vendors.apiMarket.sat.basic-url}")
    private String SAT_ENDPOINT;

    @Value("${vendors.apiMarket.renapo.basic-url}")
    private String RENAPO_ENDPOINT;

    @Value("${vendors.apiMarket.token}")
    private String API_KEY;

    private static final String VALIDATIONS_ENABLED = "VALIDATIONS_ENABLED";

    //    @Autowired
    private final RestTemplate restTemplate;

    private final SettingsDao settingsDao;

    private final ClientValidationsDao clientValidationsDao;
    private final IJWTService jwtTokenService;

    @Override
    public Object validateCurp(String curp, Client client) {
        log.info("validateCurp method :: Calling api market service to extract CURP: {}", curp);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    String.format(RENAPO_ENDPOINT + "/grupo/valida-curp?curp=%s", curp),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from valida-curp in API Market service"));


            String responseBody = responseEntity.getBody();

            var validationToSearch = clientValidationsDao.findByClient(client)
                    .orElseThrow(() -> new FinancialSystemException("validateCurp method :: No validation found for the client"));

            Long id = Optional.of(validationToSearch)
                    .map(ClientValidations::getId)
                    .orElse(null);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (jsonNode.has("data")) {
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode.has("curp")) {
                    CurpValidationResponse curpValidationResponseObject = objectMapper.treeToValue(jsonNode, CurpValidationResponse.class);
                    log.info("validateCurp method :: CURP found: {}", curpValidationResponseObject.getData().getCurp());

                    JsonNode json = new ObjectMapper().convertValue(curpValidationResponseObject, JsonNode.class);

                    clientValidationsDao.saveAndFlush(ClientValidations.builder()
                            .id(id)
                            .createdBy(client.getUsername())
                            .lista69b(validationToSearch.getLista69b())
                            .curp(json.toString())
                            .datosFiscales(validationToSearch.getDatosFiscales())
                            .ocrIne(validationToSearch.getOcrIne())
                            .rfc(validationToSearch.getRfc())
                            .client(client)
                            .build());

                    return curpValidationResponseObject;
                } else {
                    log.info("validateCurp method :: CURP data not found for CURP: {}", curp);

                    clientValidationsDao.saveAndFlush(ClientValidations.builder()
                            .id(id)
                            .createdBy(client.getUsername())
                            .lista69b(validationToSearch.getLista69b())
                            .curp(jsonNode.toString())
                            .datosFiscales(validationToSearch.getDatosFiscales())
                            .ocrIne(validationToSearch.getOcrIne())
                            .rfc(validationToSearch.getRfc())
                            .client(client)
                            .build());

                    return null;
                }
            } else {
                log.error("validateCurp method :: Unexpected response");
                throw new FinancialSystemException("validateCurp method :: Unexpected response");
            }
        } catch (Exception e) {
            log.error("validateCurp method :: Error occurred while calling the api market service to validate curp");
            throw new FinancialSystemException("Error connecting to api market service");
        }
    }

    @Override
    public RfcResponse getRfcDataContribuidor(String curp) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    String.format(SAT_ENDPOINT + "/grupo/obtener-rfc?curp=%s", curp),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from API market service"));

            String responseBody = responseEntity.getBody();



            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (jsonNode.has("data")) {
                return objectMapper.treeToValue(jsonNode, RfcResponse.class);

            } else {
                log.info("getRfcData method says: RFC not found for CURP: {}", curp);
                RfcNotFoundResponse notFoundResponse = objectMapper.treeToValue(jsonNode, RfcNotFoundResponse.class);
                return null;
            }
        } catch (Exception e) {
            log.error("getRfcData method says: Error occurred while calling the api market service to extract rfc");
            throw new FinancialSystemException("Error connecting to api market service");
        }

    }

    @Override
    public RfcResponse getRfcData(String curp, HttpServletRequest request) { // Method done and validated
        log.info("getRfcData method says: Calling api market service to extract rfc for CURP: {}", curp);
        Client client = jwtTokenService.extractClientFromToken(jwtTokenService.extractTokenFromRequest(request));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    String.format(SAT_ENDPOINT + "/grupo/obtener-rfc?curp=%s", curp),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from API market service"));

            String responseBody = responseEntity.getBody();

            var validationToSearch = clientValidationsDao.findByClient(client);

            Long id = validationToSearch.map(ClientValidations::getId).orElse(null);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (jsonNode.has("data")) {
                RfcResponse rfcResponseObject = objectMapper.treeToValue(jsonNode, RfcResponse.class);
                System.out.println("getRfcData method says: RFC found: " + rfcResponseObject.getData().getRfc());

                JsonNode json = new ObjectMapper().convertValue(rfcResponseObject, JsonNode.class);

                clientValidationsDao.saveAndFlush(ClientValidations.builder()
                        .createdBy(client.getUsername())
                        .id(id)
                        .rfc(json.toString())
                        .client(client)
                        .build());

                return rfcResponseObject;
            } else {
                log.info("getRfcData method says: RFC not found for CURP: {}", curp);
                RfcNotFoundResponse notFoundResponse = objectMapper.treeToValue(jsonNode, RfcNotFoundResponse.class);
                JsonNode json = new ObjectMapper().convertValue(notFoundResponse, JsonNode.class);

                clientValidationsDao.saveAndFlush(ClientValidations.builder()
                        .createdBy(client.getUsername())
                        .id(id)
                        .rfc(json.toString())
                        .client(client)
                        .build());
                return null;
            }
        } catch (Exception e) {
            log.error("getRfcData method says: Error occurred while calling the api market service to extract rfc");
            throw new FinancialSystemException("Error connecting to api market service");
        }
    }

    @Override
    public Object list69b(String rfc, Client client) {
        log.info("Calling api market service to extract list69b, for rfc: {}", rfc);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    String.format(SAT_ENDPOINT + "/v2/lista69b?rfc=%s", rfc),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);


            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from API market service"));

            String responseBody = responseEntity.getBody();

            var validationToSearch = clientValidationsDao.findByClient(client)
                    .orElseThrow(() -> new FinancialSystemException("No validation found for the client"));

            Long id = Optional.of(validationToSearch)
                    .map(ClientValidations::getId)
                    .orElse(null);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);


            if (jsonNode.has("data")) {
                JsonNode data = jsonNode.get("data");
                if (data.isArray() && data.isEmpty()) {
                    List69B list69bResponseObject = objectMapper.treeToValue(jsonNode, List69B.class);
                    clientValidationsDao.saveAndFlush(ClientValidations.builder()
                            .id(id)
                            .createdBy(client.getUsername())
                            .updatedBy(client.getUsername())
                            .lista69b(jsonNode.toString())
                            .curp(validationToSearch.getCurp())
                            .datosFiscales(validationToSearch.getDatosFiscales())
                            .ocrIne(validationToSearch.getOcrIne())
                            .rfc(validationToSearch.getRfc())
                            .client(client)
                            .build());

                    return list69bResponseObject;
                } else {
                    log.info("list69b method :: Data found for RFC: {}", rfc);

                    clientValidationsDao.saveAndFlush(ClientValidations.builder()
                            .id(id)
                            .createdBy(client.getUsername())
                            .updatedBy(client.getUsername())
                            .lista69b(jsonNode.toString())
                            .curp(validationToSearch.getCurp())
                            .datosFiscales(validationToSearch.getDatosFiscales())
                            .ocrIne(validationToSearch.getOcrIne())
                            .rfc(validationToSearch.getRfc())
                            .client(client)
                            .build());
                    return null;
                }

            } else {
                log.error("list69b method :: Unexpected response");
                throw new FinancialSystemException("validateCurp method :: Unexpected response");
            }
        } catch (Exception e) {
            log.error("list69b method :: Error occurred while calling the api market service to check list69b");
            throw new FinancialSystemException("Error connecting to api market service");
        }
    }

    @Override
    public Object validateRfc(String rfc, String name, int regimenClave, String cp, Client client) {
        log.info("Calling api market service to validate fiscal data for the follow rfc:: {}", rfc);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    String.format(SAT_ENDPOINT + "/grupo/validar-datos?nombre=%s&rfc=%s&regimen=%s&cp=%s", name, rfc, regimenClave, cp),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);


            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from API market service"));

            String responseBody = responseEntity.getBody();

            var validationToSearch = clientValidationsDao.findByClient(client)
                    .orElseThrow(() -> new FinancialSystemException("No validation found for the client"));

            Long id = Optional.of(validationToSearch)
                    .map(ClientValidations::getId)
                    .orElse(null);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (jsonNode.has("data")) {
                log.info("validateRfc method :: Data found for RFC: {}", rfc);
                clientValidationsDao.saveAndFlush(ClientValidations.builder()
                        .id(id)
                        .createdBy(client.getUsername())
                        .updatedBy(client.getUsername())
                        .lista69b(validationToSearch.getLista69b())
                        .curp(validationToSearch.getCurp())
                        .datosFiscales(jsonNode.toString())
                        .ocrIne(validationToSearch.getOcrIne())
                        .rfc(validationToSearch.getRfc())
                        .client(client)
                        .build());
                return null; // TODO change the return
            } else {
                log.info("validateRfc method :: Data not found for RFC: {}", rfc);
                clientValidationsDao.saveAndFlush(ClientValidations.builder()
                        .id(id)
                        .createdBy(client.getUsername())
                        .updatedBy(client.getUsername())
                        .lista69b(validationToSearch.getLista69b())
                        .curp(validationToSearch.getCurp())
                        .datosFiscales(jsonNode.toString())
                        .ocrIne(validationToSearch.getOcrIne())
                        .rfc(validationToSearch.getRfc())
                        .client(client)
                        .build());
                return null; // TODO change the return
            }
        } catch (Exception e) {
            log.error("validateRfc method :: Error occurred while calling the api market service to check datos fiscales");
            throw new FinancialSystemException("Error connecting to api market service");
        }
    }

    @Override
    public boolean validationsEnabled() {
        var settings = settingsDao.findByKey(VALIDATIONS_ENABLED).orElseThrow(() -> new FinancialSystemException("Setting not found"));
        return "true".equals(settings.getValue());
    }
}
