package com.financial.system.auth.services;

import com.financial.system.auth.models.dto.ChangePassword;
import com.financial.system.auth.models.dto.EmailVerificationRequest;
import com.financial.system.auth.models.dto.LoginRequest;
import com.financial.system.auth.models.dto.RequestChangePassword;
import com.financial.system.core.models.dto.ClientDTO;
import com.financial.system.core.models.dto.RegisterClientDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface IAuthService {
    ClientDTO registerUser(HttpServletRequest request, RegisterClientDTO registerClientDTO);

    void verifyEmail(EmailVerificationRequest request);

    String authenticate(HttpServletRequest httpServletRequest, HttpServletResponse servletResponse, LoginRequest request);

    void sendRequestChangePassword(RequestChangePassword requestChangePassword);

    void changePassword(String token, ChangePassword changePassword);

    Map<Object, Object> validateUsername(String username);
}
