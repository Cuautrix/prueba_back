package com.financial.system.security.context.jwt.service.impl;

import com.financial.system.core.models.dao.ClientDAO;
import com.financial.system.core.models.entities.Client;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTService implements IJWTService {
    private final ClientDAO clientDAO;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_ISSUER}")
    private String jwtIssuer;

    @Override
    public Client extractClientFromToken(String token){
        String username = getUsernameFromToken(token);
        return clientDAO.findByUsername(username)
                .orElseThrow(()-> new FinancialSystemException("User not found for this token"));
    }

    @Override
    public String generateToken(Long userId, long expiryTime, String type, Client client) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtIssuer)
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .issuedAt(new Date(System.currentTimeMillis()))
                .claim("type", type)
                .claim("username", client.getUsername())
                .claims(generateClaims(client))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String generateToken2(Long userId, long expiryTime, String type, Client client, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>();

        // claims fijos que NO debes perder
        claims.put("type", type);
        claims.put("username", client.getUsername());

        // claims generados automáticamente por tu lógica actual
        claims.putAll(generateClaims(client));

        // claims personalizados que tú agregas manualmente (rol, validado, etc.)
        if (additionalClaims != null) {
            claims.putAll(additionalClaims);
        }

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtIssuer)
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .issuedAt(new Date(System.currentTimeMillis()))
                .claims(claims)
                .signWith(getSecretKey())
                .compact();
    }




    @Override
    public String generateSingleToken(Long userId, long timeMillis, String type, String username) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtIssuer)
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + timeMillis))
                .issuedAt(new Date(System.currentTimeMillis()))
                .claim("type", type)
                .claim("username", username)
                .signWith(getSecretKey())
                .compact();
    }

    private Map<String, Object> generateClaims(Client client) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("VALIDATION_STATUS", client.getClientValidationStatus().getDescription().name());
        claims.put("CLIENT_TYPE", client.getClientType().getKey().name());
        claims.put("SCOPE", client.getScope());
        claims.put("STAGES", client.getOnboardingStages());
        claims.put("MFA_ENABLED", client.isMfaEnabled());



        return claims;
    }



    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    public <T> T getClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.getClaimsFromToken(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    @Override
    public boolean isAValidToken(String token) {
        return false;
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return false;
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    @Override
    public boolean isExpired(String token) {
        final var expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    @Override
    public String getUserIdFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaims(token, claims -> claims.get("username", String.class));

    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "access_token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
