package com.financial.system.auth.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.system.auth.exceptions.EmailAlreadyValidatedException;
import com.financial.system.auth.exceptions.EmailNoValidatedException;
import com.financial.system.auth.exceptions.TokenExpiredException;
import com.financial.system.auth.exceptions.TokenHasBeenUsedException;
import com.financial.system.auth.models.dto.ChangePassword;
import com.financial.system.auth.models.dto.EmailVerificationRequest;
import com.financial.system.auth.models.dto.LoginRequest;
import com.financial.system.auth.models.dto.RequestChangePassword;
import com.financial.system.auth.services.IAuthService;
import com.financial.system.core.exceptions.AccountDeletedException;
import com.financial.system.core.exceptions.ClientTypeDisabledException;
import com.financial.system.core.models.dao.*;
import com.financial.system.core.models.dto.ClientDTO;
import com.financial.system.core.models.dto.RegisterClientDTO;
import com.financial.system.core.models.entities.*;
import com.financial.system.core.models.enums.*;
import com.financial.system.core.models.mappers.ClientMapper;
import com.financial.system.core.services.IEmailService;
import com.financial.system.security.context.FSUserDetails;
import com.financial.system.security.context.jwt.enums.TokenStatus;
import com.financial.system.security.context.jwt.enums.TokenType;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.security.exceptions.AccountLockedException;
import com.financial.system.security.exceptions.AccountLockedTemporarilyException;
import com.financial.system.security.models.dao.EmailVerificationDAO;
import com.financial.system.security.models.dao.PasswordResetTokenDAO;
import com.financial.system.security.models.dto.EmailVerificationDTO;
import com.financial.system.security.models.entities.EmailVerification;
import com.financial.system.security.models.entities.PasswordResetToken;
import com.financial.system.security.utils.IPIdentifierHelper;
import com.financial.system.shared.constants.FinancialSystemMessages;
import com.financial.system.shared.enums.FinancialSystemStatus;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.shared.utils.CookieUtils;
import com.financial.system.shared.utils.FinancialSystemLogs;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j

public class AuthService implements IAuthService {
    private final ClientDAO clientDAO;
    private final IJWTService jwtService;
    private final NationalityDAO nationalityDAO;
    private final ClientTypeDAO clientTypeDAO;
    private final ClientValidationStatusDAO clientValidationStatusDAO;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;
    private final EmailVerificationDAO emailVerificationDAO;
    private final PasswordResetTokenDAO passwordResetTokenDAO;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private ClientVSDAO clientVSDAO;


    private static final int SESSION_ATTEMPTS_WARNING_NUMBER = 3;
    private static final int SESSION_ATTEMPTS_DANGEROUS_NUMBER = 4;
    private static final int MAX_SESSION_ATTEMPTS = 5;
    private static final int RESET_PASSWORD_TOKEN_TIME = 300000;

    @Value("${financial_system.domain}")
    private String domain;

    @Value("${security.jwt.accessTokenCookieName}")
    private String cookieName;


    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        return passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now());
    }


    @Override
    public void changePassword(String token, ChangePassword changePassword) {
        if (token.isEmpty()) {
            throw new FinancialSystemException("Token is empty");
        }
        // TODO validate token should be different to empty string
        // TODO validate client email verification, client approved verification, client out of onboarding
        PasswordResetToken passwordResetTokenFound = passwordResetTokenDAO.findByToken(token)
                .orElseThrow(() -> new FinancialSystemException("Token not found"));

        Client clientFound = passwordResetTokenFound.getClient();

        validatePasswordResetToken(passwordResetTokenFound, changePassword.getPassword(), clientFound);
    }

    @Override
    public Map<Object, Object> validateUsername(String username) {
        Client clientFound = clientDAO.findByUsername(username).orElseThrow(() -> new FinancialSystemException("Username not found"));
        if (!clientFound.isEmailVerified()) {
            log.error("Email has not been verified yet, for client: {}", clientFound.getUsername());
            throw new EmailNoValidatedException("Email has not been verified yet");
        }
        return Map.of("username", clientFound.getUsername(), "usernameIsValid", true);
    }

    @Override
    public void sendRequestChangePassword(RequestChangePassword requestChangePassword) {
        Client clientFound = clientDAO.findByUsername(requestChangePassword.getUsername())
                .orElseThrow(() -> new FinancialSystemException("User not found"));

        String tokenToResetPassword = jwtService.generateSingleToken(clientFound.getId(), RESET_PASSWORD_TOKEN_TIME, TokenType.RESET_PASSWORD.name(), clientFound.getUsername());

        PasswordResetToken passwordResetToken = buildPasswordResetToken(clientFound, LocalDateTime.now().plusMinutes(5), TokenStatus.ACTIVE, tokenToResetPassword);
        passwordResetTokenDAO.save(passwordResetToken);

        emailService.sendRequestChangePassword(clientFound, "Reset your password", tokenToResetPassword);
    }

    @Override
    public String authenticate(HttpServletRequest httpServletRequest, HttpServletResponse servletResponse, LoginRequest request) {
        String clientIpAddress = IPIdentifierHelper.getClientIp(httpServletRequest);

        sessionExists(servletResponse, httpServletRequest, clientIpAddress);

        log.info("[Requester: {}] Login attempt: {}", clientIpAddress, request);

        Client client = clientDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new FinancialSystemException("User not found"));

        validateIfAccountIsTemporaryLocked(client);

        try {
            validateClientStatus(client);
            validateIfAccountIsDeleted(client);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                resetFailedAttemptsIfNeeded(client);
                client.setLastSessionAttempt(LocalDateTime.now());
                clientDAO.save(client);

                // 🌟 Lógica personalizada para generar el token con claims adicionales
                Map<String, Object> claims = new HashMap<>();
                claims.put("rol", client.getRol());
                AtomicBoolean validado = new AtomicBoolean(false);

                if ("admin".equalsIgnoreCase(client.getRol())) {
                    log.info("Cliente con rol ADMIN - se marca como validado automáticamente.");
                    validado.set(true);

                } else {
                    log.info("Cliente con rol CLIENTE - buscando estado de validación en ClientVSDAO...");

                    clientVSDAO.findByClientId(client.getId()).ifPresentOrElse(status -> {
                        log.info("✅ ClientVS encontrado para cliente {}: bankInfo={}, addressInfo={}, personalInfo={}, documents={}",
                                client.getId(),
                                status.isBankInfo(),
                                status.isAddressInfo(),
                                status.isPersonalInfo(),
                                status.isDocuments()
                        );

                        if (status.isBankInfo() && status.isAddressInfo()
                                && status.isPersonalInfo() && status.isDocuments()) {
                            log.info("✅ Todos los campos están en true. Cliente validado.");
                            validado.set(true);

                        } else {
                            log.info("❌ Alguno de los campos está en false. Cliente no validado.");
                        }
                    }, () -> {
                        log.warn("❌ No se encontró ningún registro ClientVS para client_id = {}", client.getId());
                    });
                }

                claims.put("rol", client.getRol());
                log.info(client.getRol());
                claims.put("validado", validado);

                // Asegúrate que tu método generateToken tenga una versión con claims
                String tokenSession = jwtService.generateToken2(
                        client.getId(),
                        600000 * 15,
                        TokenType.ACCESS_TOKEN.name(),
                        client,
                        claims
                );

                FSUserDetails userDetails = (FSUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                log.info("Authentication token was generated for client {} new session: {}", client.getId(), tokenSession);

                return tokenSession;
            }

        } catch (BadCredentialsException e) {
            log.info(client.getClientValidationStatus().getDescription().name());
            handleFailedAuthentication(client);
            throw new FinancialSystemException("Invalid credentials");
        }

        return null;
    }


    @Override
    public ClientDTO registerUser(HttpServletRequest request, RegisterClientDTO registerClientDTO) {
        String clientIpAddress = IPIdentifierHelper.getClientIp(request);
        log.info("[Requester: {}] New client registration requested.", clientIpAddress);
        log.info(FinancialSystemLogs.LOG_SEPARATOR);

        validateAvailability(registerClientDTO.getUsername(), clientDAO::findByUsername, "Username", clientIpAddress);

        validateAvailability(registerClientDTO.getEmail(), clientDAO::findByEmail, "Email", clientIpAddress);

        Nationality clientNationality = getEntityById(registerClientDTO.getIdNationality(), nationalityDAO::findById, "Nationality");

        ClientType clientType = getEntityById(registerClientDTO.getIdClientType(), clientTypeDAO::findById, "Client type");

        ClientValidationStatus clientValidationStatus = getClientValidationStatusById();

        log.info("[Requester: {}] Starting client registration.", clientIpAddress);

        String passwordEncoded = passwordEncoder.encode(registerClientDTO.getPassword());
        Client client = ClientMapper.preRegisterToEntity(registerClientDTO, clientNationality, clientType, clientValidationStatus, ClientScope.PRE_REGISTER, passwordEncoded);

        setOnboardingStage(clientType.getKey(), client);

        Client clientCreated = clientDAO.save(client);
        log.info("[Requester: {}] New client was saved with Id: {}", clientIpAddress, clientCreated.getId());

        log.info("[Requester: {}] Sending validation email to client: {}", clientIpAddress, clientCreated.getEmail());
        emailService.sendEmail(clientCreated, "Verify your account");

        return ClientMapper.toDTO(clientCreated);
    }

    @Override
    public void verifyEmail(EmailVerificationRequest request) {
        String encodedData = request.getEncodedData();
        log.info("Encoded data: {}", encodedData);

        try {
            String decodedData = decodeEncodedData(encodedData);
            EmailVerificationDTO emailVerificationDecoded = parseDecodedData(decodedData);

            Client client = clientDAO.findById(emailVerificationDecoded.getClientId())
                    .orElseThrow(() -> new FinancialSystemException("Client not found"));
            checkEmailVerified(client);

            EmailVerification code = findVerificationCode(emailVerificationDecoded);
            validateVerificationCode(code);

            markEmailAsVerifiedAndSettingScope(code, client);

        } catch (IllegalArgumentException e) {
            log.error("Failed to decode Base64 encoded data", e);
            throw new FinancialSystemException("Invalid Base64 encoded data");
        } catch (Exception e) {
            throw new FinancialSystemException(e.getMessage());
        }
    }

    // Private utility methods here:

    private void validateSessionAttempts(Client client) {
        if (!client.isAccountLocked()) {
            updateFailedAttempts(client);

            if (client.getFailedAttempts() >= MAX_SESSION_ATTEMPTS) {
                lockAccount(client);
            } else if (client.getFailedAttempts() == SESSION_ATTEMPTS_WARNING_NUMBER) {
                applyTemporaryLock(client, 10);
            } else if (client.getFailedAttempts() == SESSION_ATTEMPTS_DANGEROUS_NUMBER) {
                applyTemporaryLock(client, 20);
            } else {
                throw new FinancialSystemException("Invalid password");
            }
        }
    }

    private void updateFailedAttempts(Client client) {
        int failedAttempts = client.getFailedAttempts();
        client.setFailedAttempts(failedAttempts == 0 ? 1 : failedAttempts + 1);
        client.setLastSessionAttempt(LocalDateTime.now());
        client.setUpdatedBy(client.getUsername());
        clientDAO.saveAndFlush(client);
    }

    private void lockAccount(Client client) {
        client.setAccountLocked(true);
        client.setUpdatedBy(client.getUsername());
        clientDAO.save(client);
        throw new AccountLockedException(FinancialSystemMessages.INVALID_CREDENTIALS + client.getFailedAttempts() + " times. Your account has been locked", FinancialSystemStatus.SESSION_ATTEMPT_LIMIT_EXCEEDED);
    }

    private void applyTemporaryLock(Client client, int lockMinutes) {
        client.setLockTime(LocalDateTime.now().plusMinutes(lockMinutes));
        client.setUpdatedBy(client.getUsername());
        Client clientSaved = clientDAO.saveAndFlush(client);

        Duration remainingTime = Duration.between(LocalDateTime.now(), clientSaved.getLockTime());
        String formattedTime = formatDuration(remainingTime);
        throw new AccountLockedTemporarilyException(
                FinancialSystemMessages.INVALID_CREDENTIALS + clientSaved.getFailedAttempts() + " times. Your account is at risk of being locked temporarily. Try again in: " + formattedTime,
                formattedTime
        );
    }

    private void sessionExists(HttpServletResponse servletResponse, HttpServletRequest httpServletRequest, String clientIpAddress) {
        Cookie cookie = CookieUtils.checkSession(httpServletRequest, cookieName);
        if (Objects.nonNull(cookie)) {
            log.info("[Requester: {}] User already authenticated.", clientIpAddress);
            CookieUtils.clear(servletResponse, cookieName, domain);
        }
    }

    private void validateIfAccountIsTemporaryLocked(Client client) {
        if (Objects.isNull(client.getLockTime())) {
            return;
        }
        if (isLockTimeExpired(client)) {
            unlockAccount(client);
        } else if (client.isAccountLocked()) {
            throw new AccountLockedException("Your account is locked, contact support", FinancialSystemStatus.ACCOUNT_LOCKED);
        }
    }


    private boolean isLockTimeExpired(Client client) {
        return LocalDateTime.now().isAfter(client.getLockTime());
    }

    private void unlockAccount(Client client) {
        client.setLockTime(null);
        client.setFailedAttempts(0);
        clientDAO.saveAndFlush(client);
    }

    private void throwAccountLockedTemporarilyException(Client client) {
        Duration remainingTime = Duration.between(LocalDateTime.now(), client.getLockTime());
        String formattedTime = formatDuration(remainingTime);
        throw new AccountLockedTemporarilyException("Your account is locked, try again in: ", formattedTime);
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60);
    }

    private void validateAvailability(String value, Function<String, Optional<?>> daoMethod, String fieldName, String ipAddress) {
        daoMethod.apply(value).ifPresent(entity -> {
            log.error("[Requester: {}] {} is not available.", ipAddress, fieldName);
            throw new FinancialSystemException(fieldName + " already taken");
        });
        log.info("[Requester: {}] {} is OK.", ipAddress, fieldName);
    }

    private <T> T getEntityById(Long id, Function<Long, Optional<T>> daoMethod, String entityName) {
        return daoMethod.apply(id)
                .orElseThrow(() -> new FinancialSystemException(entityName + " not found in our catalog"));
    }

    private ClientValidationStatus getClientValidationStatusById() {
        return clientValidationStatusDAO.getClientValidationStatusByDescription(ClientStatusEnum.ONBOARDING)
                .orElseThrow(() -> new FinancialSystemException("Client validation status not found in our catalog"));
    }

    private void setOnboardingStage(ClientTypeEnum clientType, Client client) {
        if (ClientTypeEnum.PFAE.equals(clientType)) {
            client.setOnboardingStages(OnboardingStage.INFO_PFAE.name());
        } else if (ClientTypeEnum.PMORAL.equals(clientType)) {
            client.setOnboardingStages(OnboardingStage.REPRESENTANTE_LEGAL.name());
        } else {
            throw new FinancialSystemException("Type client not found in our catalog");
        }
    }

    private String decodeEncodedData(String encodedData) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
        String decodedData = new String(decodedBytes, StandardCharsets.UTF_8);
        log.info("Decoded data: {}", decodedData);
        return decodedData;
    }

    private EmailVerificationDTO parseDecodedData(String decodedData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(decodedData, EmailVerificationDTO.class);
    }

    private void checkEmailVerified(Client client) {
        if (client.isEmailVerified()) {
            throw new EmailAlreadyValidatedException("Email already validated");
        }
    }

    private EmailVerification findVerificationCode(EmailVerificationDTO emailVerificationDecoded) {
        return emailVerificationDAO.findByVerificationTokenAndClientId(
                        emailVerificationDecoded.getCode(), emailVerificationDecoded.getClientId())
                .orElseThrow(() -> new FinancialSystemException(FinancialSystemMessages.CODE_DOES_NOT_MATCH));
    }

    private void validateVerificationCode(EmailVerification code) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(code.getExpirationDate())) {
            code.setCanceled(true);
            emailVerificationDAO.save(code);
            throw new FinancialSystemException(FinancialSystemMessages.CODE_EXPIRED);
        }

        if (code.isCanceled()) {
            throw new FinancialSystemException(FinancialSystemMessages.CODE_CANCELED);
        }

        if (code.isValidated()) {
            throw new FinancialSystemException(FinancialSystemMessages.CODE_ALREADY_VALIDATED);
        }
    }

    private void markEmailAsVerifiedAndSettingScope(EmailVerification code, Client client) {
        code.setValidated(true);
        emailVerificationDAO.save(code);
        client.setEmailVerified(true);

        if (ClientTypeEnum.PFAE.equals(client.getClientType().getKey())) {
            client.setScope(
                    NationalityEnum.MEXICAN.equals(client.getNationality().getDescription())
                            ? ClientScope.ONBOARDING_PFAE_LOCAL.name()
                            : ClientScope.ONBOARDING_PFAE_INTERNATIONAL.name());
        } else {
            client.setScope(
                    NationalityEnum.MEXICAN.equals(client.getNationality().getDescription())
                            ? ClientScope.ONBOARDING_PMORAL_LOCAL.name()
                            : ClientScope.ONBOARDING_PMORAL_INTERNATIONAL.name());
        }

        client.setUpdatedBy(client.getUsername());
        clientDAO.save(client);
    }

    private void validateClientStatus(Client client) {
        if (!client.isEmailVerified()) {
            throw new EmailNoValidatedException(FinancialSystemMessages.EMAIL_NO_VALIDATED);
        }

        if (client.isAccountLocked()) {
            throw new AccountLockedException(FinancialSystemMessages.NO_ACCESS, FinancialSystemStatus.ACCOUNT_LOCKED);
        }

        if (!client.getNationality().isActive()) {
            throw new ClientTypeDisabledException("The clients with nationality " + client.getNationality().getDescription().name() + "are disabled");
        }
    }

    private void validateIfAccountIsDeleted(Client client) {
        if (Boolean.TRUE.equals(client.getAccountDeleted())) {
            log.error("Account is deleted");
            throw new AccountDeletedException("Your account has been deleted, you dont have access to the resources");
        }
    }

    private void handleFailedAuthentication(Client client) {
        log.info(client.getClientValidationStatus().getDescription().name());

        // Only check session attempts if the client status is APPROVED or COMPLETED
        if (ClientStatusEnum.APPROVED.equals(client.getClientValidationStatus().getDescription()) ||
                ClientStatusEnum.COMPLETED.equals(client.getClientValidationStatus().getDescription())) {
            validateSessionAttempts(client);
        }
        validateSessionAttempts(client);
    }

    private void resetFailedAttemptsIfNeeded(Client client) {
        if (client.getFailedAttempts() > 0) {
            client.setFailedAttempts(0);
            client.setUpdatedBy(client.getUsername());
            clientDAO.save(client);
        }
    }

    private PasswordResetToken buildPasswordResetToken(Client client, LocalDateTime expiryDate, TokenStatus tokenStatus, String token) {
        return PasswordResetToken.builder()
                .client(client)
                .expiryDate(expiryDate)
                .status(tokenStatus)
                .token(token)
                .build();
    }

    private void validatePasswordResetToken(PasswordResetToken passwordResetTokenFound, String password, Client clientFound) {
        if (passwordResetTokenFound.getStatus().equals(TokenStatus.EXPIRED)) {
            throw new TokenExpiredException("Token expired");
        } else if (passwordResetTokenFound.getStatus().equals(TokenStatus.USED)) {
            throw new TokenHasBeenUsedException("Token has been used");
        } else if (isTokenExpired(passwordResetTokenFound) && passwordResetTokenFound.getStatus().equals(TokenStatus.ACTIVE)) {
            passwordResetTokenFound.setStatus(TokenStatus.EXPIRED);
            passwordResetTokenDAO.save(passwordResetTokenFound);
            throw new TokenExpiredException("Token expired");
        }
        if (!isTokenExpired(passwordResetTokenFound) && passwordResetTokenFound.getStatus().equals(TokenStatus.ACTIVE)) {
            String passwordEncoded = passwordEncoder.encode(password);
            clientFound.setPassword(passwordEncoded);
            this.clientDAO.save(clientFound);

            passwordResetTokenFound.setStatus(TokenStatus.USED);
            passwordResetTokenDAO.save(passwordResetTokenFound);
        }
    }
}
