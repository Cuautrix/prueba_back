package com.financial.system.vendors.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.system.core.models.dao.ClientValidationsDao;
import com.financial.system.core.models.dto.request.OCRDto;
import com.financial.system.core.models.dto.response.OCRResponse;
import com.financial.system.core.models.dto.response.api_market_responses.RfcResponse;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.models.entities.ClientValidations;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.vendors.IAPIMarketService;
import com.financial.system.vendors.IOCRService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class OCRService implements IOCRService {

    @Value("${vendors.ocr.url}")
    private String OCR_ENDPOINT;

    @Autowired
    private RestTemplate restTemplate;

    private final IAPIMarketService apiMarketService;
    private final IJWTService jwtTokenService;
    private final ClientValidationsDao clientValidationsDao;

    @Override
    public OCRResponse extractIneData(OCRDto ocrDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer eyJ4NXQiOiJPREJtTVRVMFpqSmpPREprTkdZMVpUaG1ZamsyWVRZek56UmpZekl6TVRCbFlqRTBNV0prWTJJeE5qZzNPRGRqWVdRNVpXWmhOV0kwTkRBM1pqTTROUSIsImtpZCI6Ik9EQm1NVFUwWmpKak9ESmtOR1kxWlRobVlqazJZVFl6TnpSall6SXpNVEJsWWpFME1XSmtZMkl4TmpnM09EZGpZV1E1WldaaE5XSTBOREEzWmpNNE5RX1JTMjU2IiwidHlwIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI1YzJmYWYyMy1kMjVkLTQ5YzctYjg2Mi00NDdlZDJjYTA2NjUiLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6IkFTQnBDWFhvTUE0a3E0NHRjYm1FalpobnU3RWEiLCJuYmYiOjE3NDQ0MTI0NDIsImF6cCI6IkFTQnBDWFhvTUE0a3E0NHRjYm1FalpobnU3RWEiLCJzY29wZSI6ImRlZmF1bHQiLCJpc3MiOiJodHRwczovL2RldmFwaWNhbWFya2V0LmNvbTo5NDQzL29hdXRoMi90b2tlbiIsImV4cCI6MTc1NzU1MjQ0MiwiaWF0IjoxNzQ0NDEyNDQyLCJqdGkiOiJiOTg3ZjMwNC1lNmM4LTQ3NzQtOTk2NS02NGFkMzViNDkzMTQiLCJjbGllbnRfaWQiOiJBU0JwQ1hYb01BNGtxNDR0Y2JtRWpaaG51N0VhIn0.jE7o7bndw2NXWKBXOzD-yUEIgMlESDuveXjN9tkjYvRNJAEDeOkPL0P6m-PXka9DmLUlNHNgvRBIsfpYo-77C2ZLPA5HCcoTGkAGMLk0fD75LPAFg-uUVNPjUeQ5mP7fDUmqf89dCXce9OE35HlUrYjKR0mdWgoIYFLXKW1cs-yqtPW167ZhJC_BTzEJElReKAmBT4LDclsSmnihH1fQz4v2utVtpZJnNUtMhDPBhnsNSu2Y0n90i7_8Wc3U77Rijp86FXDLOyXwlAra-kltskRZADfLV465Rz1SSttxTuGnuMKoEU5SWI7X7jhcpj5cvrpqVh3DbxLlsVZAq1DXtA");

        HttpEntity<OCRDto> requestEntity = new HttpEntity<>(ocrDto, headers);

        try {
            ResponseEntity<OCRResponse> responseEntity = restTemplate.exchange(
                    OCR_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    OCRResponse.class);

            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from OCR service"));

            log.info("Response from OCR service: {}", responseEntity.getBody().getCurp());


            RfcResponse rfcResponse = apiMarketService.getRfcDataContribuidor(responseEntity.getBody().getCurp());

            responseEntity.getBody().setRfc(Optional.ofNullable(rfcResponse)
                    .map(RfcResponse::getData)
                    .map(RfcResponse.Data::getRfc)
                    .orElse(null));

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("Error occurred while calling the OCR service", e);
            throw new FinancialSystemException("Error connecting to OCR service");
        }
    }

    @Override
    public OCRResponse extractData(OCRDto ocrDto, HttpServletRequest request) {
        log.info("Calling OCR service to extract data.");
        Client client = jwtTokenService.extractClientFromToken(jwtTokenService.extractTokenFromRequest(request));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer eyJ4NXQiOiJPREJtTVRVMFpqSmpPREprTkdZMVpUaG1ZamsyWVRZek56UmpZekl6TVRCbFlqRTBNV0prWTJJeE5qZzNPRGRqWVdRNVpXWmhOV0kwTkRBM1pqTTROUSIsImtpZCI6Ik9EQm1NVFUwWmpKak9ESmtOR1kxWlRobVlqazJZVFl6TnpSall6SXpNVEJsWWpFME1XSmtZMkl4TmpnM09EZGpZV1E1WldaaE5XSTBOREEzWmpNNE5RX1JTMjU2IiwidHlwIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI1YzJmYWYyMy1kMjVkLTQ5YzctYjg2Mi00NDdlZDJjYTA2NjUiLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6IkFTQnBDWFhvTUE0a3E0NHRjYm1FalpobnU3RWEiLCJuYmYiOjE3NDM3MDQxNDIsImF6cCI6IkFTQnBDWFhvTUE0a3E0NHRjYm1FalpobnU3RWEiLCJzY29wZSI6ImRlZmF1bHQiLCJpc3MiOiJodHRwczovL2RldmFwaWNhbWFya2V0LmNvbTo5NDQzL29hdXRoMi90b2tlbiIsImV4cCI6MTc1Njg0NDE0MiwiaWF0IjoxNzQzNzA0MTQyLCJqdGkiOiI2YjhkNjJiMS05OWQzLTRmMDktODQ4My04ZjMzZTc3Mzc0NmUiLCJjbGllbnRfaWQiOiJBU0JwQ1hYb01BNGtxNDR0Y2JtRWpaaG51N0VhIn0.mvBA-vpFhb-tF0k8yhbn2uBW_o2pyloLE-1dxFmWi7c-JU2MrsuoEzxrRs93vVLvoFN6KP4S6sx-yeZBh3d_cmK0Uu-JHZeeZgEh50lG0zzKHh33q0YTG4UCty-fNpPoP0AXpemoVZNlbIPx19UJdKS3sFIHc9ORQcGd6WmryEU8-fGC1YVt64FwCu5wIDTd-n5YcmESHr1DBHQRsWhEGXqMIJGf1BWE0PZhrpcGl70vKUxsYT6szwm7sD9T_BwWlyedX8IUvl9YIXLNu5o5AGPfM6IKwT0zpHa6lzd0Oq8CqdbUziwUDQ6LbL8ShuM0FTItLTpY6wv3dZxmLwwukQ");

        HttpEntity<OCRDto> requestEntity = new HttpEntity<>(ocrDto, headers);

        try {
            ResponseEntity<OCRResponse> responseEntity = restTemplate.exchange(
                    OCR_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    OCRResponse.class);

            Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new FinancialSystemException("No response from OCR service"));

            log.info("Response from OCR service: {}", responseEntity.getBody().getCurp());


            RfcResponse rfcResponse = apiMarketService.getRfcData(responseEntity.getBody().getCurp(), request); // Calling the API Market service to get RFC data

            responseEntity.getBody().setRfc(Optional.ofNullable(rfcResponse)
                    .map(RfcResponse::getData)
                    .map(RfcResponse.Data::getRfc)
                    .orElse(null));

            var validationToSearch = clientValidationsDao.findByClient(client)
                    .orElseThrow(() -> new FinancialSystemException("No validation found for the client"));

            Long id = Optional.of(validationToSearch)
                    .map(ClientValidations::getId)
                    .orElse(null);

            var json = new ObjectMapper().convertValue(responseEntity.getBody(), JsonNode.class);

            clientValidationsDao.saveAndFlush(ClientValidations.builder()
                    .createdBy(client.getUsername())
                    .id(id)
                    .rfc(validationToSearch.getRfc())
                    .ocrIne(json.toString())
                    .client(client)
                    .build());

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("Error occurred while calling the OCR service", e);
            throw new FinancialSystemException("Error connecting to OCR service");
        }
    }
}
