package com.financial.system.core.models.dto.response.api_market_responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RfcResponse {
    private Data data;
    private int status;
    private String message;
    private boolean success;
    private String codigoValidacion;

    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Data
    public static class Data {
        private String rfc;
    }
}
