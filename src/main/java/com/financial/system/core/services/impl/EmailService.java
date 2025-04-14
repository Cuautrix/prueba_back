package com.financial.system.core.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.services.IEmailService;
import com.financial.system.security.context.jwt.enums.TokenType;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.security.models.dao.EmailVerificationDAO;
import com.financial.system.security.models.dto.EmailVerificationDTO;
import com.financial.system.security.models.entities.EmailVerification;
import com.financial.system.shared.exceptions.FinancialSystemException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {
    public static final String FINANCIAL_SYSTEM_NOTIFICATIONS_MAIL = "femm15.mm@gmail.com";
    private final EmailVerificationDAO emailVerificationDao;
    private final JavaMailSender mailSender;
    private final IJWTService jwtService;

    @Value("${financial_system.ws.endpoint}")
    private String validationEndpoint;

    @Value("${financial_system.ws.password-recovery-endpoint}")
    private String passwordRecoveryEndpoint;

    @Override
    public void sendEmail(Client client, String subject) {
        try {
            String body = readHtmlTemplate("email-templates/email-verification-template.html");
            body = replacePlaceholders(body, UUID.randomUUID().toString());
            body = replacePlaceholdersUrl(body, buildEmailVerificationCode(client));

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(client.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(FINANCIAL_SYSTEM_NOTIFICATIONS_MAIL);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", client.getEmail(), e.getMessage());
        }

    }


    @Override
    public void sendRequestChangePassword(Client client, String subject, String token) {
        try {
            String body = readHtmlTemplate("email-templates/request-reset-password-template.html");
            body = replacePlaceholders(body, UUID.randomUUID().toString());
            body = replacePlaceholdersUrl(body, buildVerificationUri(token));

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(client.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(FINANCIAL_SYSTEM_NOTIFICATIONS_MAIL);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", client.getEmail(), e.getMessage());
        }

    }

    private String readHtmlTemplate(String path) throws IOException {
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String replacePlaceholders(String body, String code) {
        return body.replace("${code}", code);
    }

    private String replacePlaceholdersUrl(String body, String validationUrl) {
        return body.replace("${validationUrl}", validationUrl);
    }

    private String buildEmailVerificationCode(Client client) throws FinancialSystemException, JsonProcessingException {
        log.info("4");
        // Generate UUID v4 token
        String verificationCode = UUID.randomUUID().toString();

        // Time to live 2 hours from now
        LocalDateTime expirationDate = LocalDateTime.now().plusHours(2);

        // Guardar registro en la base de datos
        EmailVerification emailVerification = createEmailVerification(client, verificationCode, expirationDate);
        emailVerificationDao.save(emailVerification);

        // Generar dto cifrado
        String encodedData = generateEncodedVerificationData(client, verificationCode);

        // Generar URL de verificación
        String emailVerificationUri = buildVerificationUri(encodedData);

        log.info("Verification Code URL: {}", emailVerificationUri);
        return emailVerificationUri;
    }

    private String buildVerificationUri(String encodedData) {
        return validationEndpoint + "?q=" + encodedData;
    }

    private EmailVerification createEmailVerification(Client client, String verificationCode, LocalDateTime expirationDate) {
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setClientId(client.getId());
        emailVerification.setVerificationToken(verificationCode);
        emailVerification.setExpirationDate(expirationDate);
        emailVerification.setCreatedAt(LocalDateTime.now());
        emailVerification.setCreatedBy(client.getUsername());
        return emailVerification;
    }

    private String generateEncodedVerificationData(Client client, String verificationCode) throws JsonProcessingException {
        EmailVerificationDTO verificationBase = new EmailVerificationDTO();
        verificationBase.setEmail(client.getEmail());
        verificationBase.setCode(verificationCode);
        verificationBase.setClientId(client.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String initialData = objectMapper.writeValueAsString(verificationBase);

        return Base64.getEncoder().encodeToString(initialData.getBytes(StandardCharsets.UTF_8));
    }
}
