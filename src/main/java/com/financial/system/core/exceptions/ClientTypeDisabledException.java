package com.financial.system.core.exceptions;

import com.financial.system.shared.enums.FinancialSystemStatus;

public class ClientTypeDisabledException extends RuntimeException {
    private final FinancialSystemStatus financialSystemStatus = FinancialSystemStatus.CLIENT_TYPE_DISABLED;
    public ClientTypeDisabledException(String message) {
        super(message);
    }
}
