package com.financial.system.shared;

import com.financial.system.shared.payload.FinancialSystemResponse;
import org.springframework.http.ResponseEntity;

public abstract class ResponseGenerator {
    public static ResponseEntity<FinancialSystemResponse> generateResponse(String message, Object data, org.springframework.http.HttpStatus status, int code) {
        return ResponseEntity.ok(FinancialSystemResponse.builder()
                .message(message)
                .status(status)
                .code(code)
                .data(data)
                .build());
    }
}
