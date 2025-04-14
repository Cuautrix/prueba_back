package com.financial.system.security.services.impl;

import com.financial.system.core.models.dao.ClientDAO;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.models.enums.ClientStatusEnum;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.security.services.ITOTPService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.shared.utils.FinancialSystemLogs;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TOTPService implements ITOTPService {
    private final IJWTService jwtService;
    private final ClientDAO clientDAO;


    public static final String MFA_VALID = "MFA Valid ::: {}";
    private static final String QR = "PNG";
    public static final String OTP_AUTH_URL_FORMAT = "otpauth://totp/%s:%s?secret=%s&issuer=%s";
    private final SecretGenerator secretGenerator;

    private static final List<ClientStatusEnum> CLIENT_VALID_STATUSES = List.of(
            ClientStatusEnum.APPROVED,
            ClientStatusEnum.COMPLETED
    );

    @Override
    public void enableMfa(HttpServletRequest httpServletRequest) throws FinancialSystemException {
        Client client = jwtService.extractClientFromToken(jwtService.extractTokenFromRequest(httpServletRequest));
        client.setMfaEnabled(true);
        try {
            clientDAO.saveAndFlush(client);
        } catch (Exception e) {
            throw new FinancialSystemException("Error al habilitar MFA para el cliente");
        }
    }

    private static void logCurrentTime() {
        TimeZone tz = TimeZone.getDefault();
        log.info("Default Time Zone: {}", tz.getID());
        log.info("System's TimeZone: {}", TimeZone.getDefault().getID());
        log.info("TimeZone Name: {}", tz.getDisplayName());
        log.info("Current Time UTC: {}", Instant.now());
        log.info("Current Time Mx: {}", LocalDateTime.now());
    }

    @Override
    public boolean verifySingleCode(HttpServletRequest servletRequest, String code) throws FinancialSystemException {
        log.info(FinancialSystemLogs.LOG_SEPARATOR);
        Client client = jwtService.extractClientFromToken(jwtService.extractTokenFromRequest(servletRequest));
        log.info("Validating user OTP code");

        logCurrentTime();

        log.info("OTP ::: {}", code);

        if (!client.isActive()) {
            throw new FinancialSystemException("User is not active");
        }
        if (!CLIENT_VALID_STATUSES.contains(client.getClientValidationStatus().getDescription()))
            throw new FinancialSystemException("Client has not been approved yet");

        String secretKey = client.getMfaSecret();
        log.info("MFA Secret Key ::: {}", secretKey);

        boolean isCodeValid = isCodeValid(code, secretKey);

        log.info(MFA_VALID, isCodeValid);

        return isCodeValid;

    }


    @Override
    public boolean validateCode(HttpServletRequest httpServletRequest, String mfaCode) throws FinancialSystemException {
        log.info(FinancialSystemLogs.LOG_SEPARATOR);
        Client client = jwtService.extractClientFromToken(jwtService.extractTokenFromRequest(httpServletRequest));
        log.info("Validating user OTP code");

        logCurrentTime();

        log.info("OTP ::: {}", mfaCode);

        String secretKey = client.getMfaSecret();

        log.info("MFA Secret Key ::: {}", secretKey);

        boolean isCodeValid = isCodeValid(mfaCode, secretKey);

        if (!isCodeValid) {
            log.error(MFA_VALID, isCodeValid);
            throw new FinancialSystemException("The code entered is not valid.");
        }

        log.info(MFA_VALID, isCodeValid);

        return isCodeValid;
    }

    @Override
    public Map<String, String> createQRCodeUsingGoogle(HttpServletRequest request) throws FinancialSystemException {
        log.info(FinancialSystemLogs.LOG_SEPARATOR);

        Client client = jwtService.extractClientFromToken(jwtService.extractTokenFromRequest(request));
        String issuerName = "Nebula";

        log.info("Building QR for MFA request");
        log.info("Issuer name ::: {}", issuerName);

        if (client.isMfaEnabled()) {
            throw new FinancialSystemException("User " + client.getUsername() + " already has MFA enabled.");
        }

        try {
            String otpAuthURL = createOTPAuthURL(client, issuerName);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(otpAuthURL, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, QR, outputStream);
            String qrCode = Base64.encodeBase64String(outputStream.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("qr", qrCode);
            response.put("secret", client.getMfaSecret());
            response.put("message", "QRCode successfully generated");
            log.info("QRCode generated");
            return response;
        } catch (Exception ex) {
            log.info("error to generate QR code");
            throw new FinancialSystemException(ex.getMessage());
        }
    }


    private String createOTPAuthURL(Client client, String issuerName) {
        if (Objects.isNull(client.getMfaSecret()) || client.getMfaSecret().isEmpty()) {
            String secretKey = this.secretGenerator.generate();
            client.setMfaSecret(secretKey);
        }

        client.setMfaEnabled(false); // Awaiting 2 codes validation
        this.clientDAO.save(client);

        log.info("Secret Key ::: {}", client.getMfaSecret());

        String urlEncodedIssuer = URLEncoder.encode(issuerName, StandardCharsets.UTF_8);
        String urlEncodedAccount = URLEncoder.encode(client.getEmail(), StandardCharsets.UTF_8);
        return String.format(OTP_AUTH_URL_FORMAT, urlEncodedIssuer, urlEncodedAccount, client.getMfaSecret(), urlEncodedIssuer);
    }


    private boolean isCodeValid(String mfaCode, String secretKey) {
        TimeProvider timeProvider = new SystemTimeProvider();
        log.info("TimeProvider Current Time (ms): {}", timeProvider.getTime());
        log.info("System Current Time (ms): {}", System.currentTimeMillis());
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setAllowedTimePeriodDiscrepancy(1); // the code will be accepted just for 30 seconds before and after the current time
        return verifier.isValidCode(secretKey, mfaCode);
    }
}
