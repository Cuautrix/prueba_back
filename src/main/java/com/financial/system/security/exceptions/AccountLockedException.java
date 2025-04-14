package com.financial.system.security.exceptions;

import com.financial.system.shared.enums.FinancialSystemStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountLockedException extends RuntimeException {
    private final FinancialSystemStatus status;
    private final HttpStatus httpStatus = HttpStatus.LOCKED;

    public AccountLockedException(String message, FinancialSystemStatus status) {
        super(message);
        this.status = status;
    }

}
