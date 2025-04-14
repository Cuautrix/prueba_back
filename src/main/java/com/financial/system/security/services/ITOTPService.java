package com.financial.system.security.services;

import com.financial.system.shared.exceptions.FinancialSystemException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface ITOTPService {
    boolean validateCode(HttpServletRequest httpServletRequest, String mfaCode) throws FinancialSystemException;

    void enableMfa(HttpServletRequest servletRequest) throws FinancialSystemException;

    Map<String, String> createQRCodeUsingGoogle(HttpServletRequest request) throws FinancialSystemException;

    boolean verifySingleCode(HttpServletRequest servletRequest, String code) throws FinancialSystemException;
}
