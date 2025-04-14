package com.financial.system.security.exceptions;

import com.financial.system.shared.enums.FinancialSystemStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AccountLockedTemporarilyException extends RuntimeException {
    private String lockedUntil;
    private FinancialSystemStatus status = FinancialSystemStatus.TEMPORARY_LOCKED;
    private final HttpStatus httpStatus = HttpStatus.LOCKED;

    public AccountLockedTemporarilyException(String message, String lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }
}
