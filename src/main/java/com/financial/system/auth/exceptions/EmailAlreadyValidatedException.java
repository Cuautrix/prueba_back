package com.financial.system.auth.exceptions;

import com.financial.system.shared.enums.FinancialSystemStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class EmailAlreadyValidatedException extends RuntimeException {
    private final FinancialSystemStatus financialSystemStatus;
    private final HttpStatusCode httpStatusCode;

    public EmailAlreadyValidatedException(String message) {
        super(message);
        this.financialSystemStatus = FinancialSystemStatus.EMAIL_ALREADY_VALIDATED;
        this.httpStatusCode = HttpStatus.BAD_REQUEST;
    }
}
