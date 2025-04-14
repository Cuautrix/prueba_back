package com.financial.system.auth.exceptions;

import com.financial.system.shared.enums.FinancialSystemStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class EmailNoValidatedException extends RuntimeException {
    private final FinancialSystemStatus financialSystemStatus;
    private final HttpStatusCode httpStatusCode;

    public EmailNoValidatedException(String message) {
        super(message);
        this.financialSystemStatus = FinancialSystemStatus.EMAIL_NO_VALIDATED;
        this.httpStatusCode = HttpStatus.BAD_REQUEST;
    }
}
