package com.financial.system.security.context.jwt.service;

import com.financial.system.core.models.entities.Client;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.function.Function;

public interface IJWTService {
    String generateToken(Long userId, long timeMillis, String type, Client client);

    String generateToken2(Long userId, long expiryTime, String type, Client client, Map<String, Object> additionalClaims);


    String generateSingleToken(Long userId, long timeMillis, String type, String username);

    boolean isAValidToken(String token);

    boolean validateRefreshToken(String token);

    <T> T getClaims(String token, Function<Claims, T> claimsResolver);

    boolean isExpired(String token);

    String getUserIdFromToken(String token);

    String getUsernameFromToken(String token);

    String extractTokenFromRequest(HttpServletRequest request);

    Client extractClientFromToken(String token);
}
