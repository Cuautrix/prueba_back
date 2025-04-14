package com.financial.system.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class FinancialSystemException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final HttpStatusCode httpStatusCode;

    public FinancialSystemException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.httpStatusCode = HttpStatus.BAD_REQUEST;
    }
}
