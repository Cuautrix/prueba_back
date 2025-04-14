package com.financial.system.auth.controllers;


import com.financial.system.auth.models.dto.*;
import com.financial.system.auth.services.IAuthService;
import com.financial.system.core.models.dto.ClientDTO;
import com.financial.system.core.models.dto.RegisterClientDTO;
import com.financial.system.security.services.ITOTPService;
import com.financial.system.shared.ResponseGenerator;
import com.financial.system.shared.constants.ApiPathConstants;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.shared.payload.FinancialSystemResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE)
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final ITOTPService itotpService;


    @PostMapping(value = "/sign-up")
    public ResponseEntity<FinancialSystemResponse> registerUser(HttpServletRequest request, @Valid @RequestBody RegisterClientDTO registerClientDTO) {
        ClientDTO clientCreated = authService.registerUser(request, registerClientDTO);
        return ResponseGenerator.generateResponse("User registered successfully", clientCreated, HttpStatus.CREATED, HttpStatus.CREATED.value());
    }

    @PostMapping(value = "/verify-email")
    public ResponseEntity<FinancialSystemResponse> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request);
        return ResponseGenerator.generateResponse("Email verified successfully", null, HttpStatus.OK, HttpStatus.OK.value());
    }

    @PostMapping(value = "/validate-username")
    public ResponseEntity<FinancialSystemResponse> validateUsername(@Valid @RequestBody UsernameValidation username) {
        Map<Object, Object> result = authService.validateUsername(username.getUsername());
        return ResponseGenerator.generateResponse("Username validation", result, HttpStatus.OK, HttpStatus.OK.value());
    }

    @PostMapping(value = "/sign-in")
    public ResponseEntity<FinancialSystemResponse> signIn(HttpServletRequest request, HttpServletResponse servletResponse, @Valid @RequestBody LoginRequest loginRequest) {
        String accessToken = authService.authenticate(request, servletResponse, loginRequest);
        return ResponseGenerator.generateResponse("User signed in successfully", Map.of("accessToken", accessToken), HttpStatus.OK, HttpStatus.OK.value()); // TODO change the response
    }

    @PostMapping(value = "/request-reset-password")
    public ResponseEntity<FinancialSystemResponse> requestResetPassword(@Valid @RequestBody RequestChangePassword requestChangePassword) {
        authService.sendRequestChangePassword(requestChangePassword);
        return ResponseGenerator.generateResponse("Link to reset password was successfully sent", null, HttpStatus.OK, HttpStatus.OK.value()); // TODO change the response
    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<FinancialSystemResponse> changePassword(@RequestParam String token, @Valid @RequestBody ChangePassword changePassword) {
        authService.changePassword(token, changePassword);
        return ResponseGenerator.generateResponse("Password was updated", null, HttpStatus.OK, HttpStatus.OK.value()); // TODO change the response
    }

    @GetMapping("/generate-qr-code-authenticator")
    public ResponseEntity<?> generateQRCodeForAuthentication(HttpServletRequest servletRequest) throws FinancialSystemException, IOException {
        return ResponseEntity.ok(itotpService.createQRCodeUsingGoogle(servletRequest));
    }

}
