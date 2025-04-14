package com.financial.system.core.models.dto.response.api_market_responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RfcNotFoundResponse {
    private boolean success;
    private String codigoValidacion;
    private String message;
    private int status;
}
