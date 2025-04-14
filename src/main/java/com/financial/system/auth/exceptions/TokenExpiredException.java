package com.financial.system.auth.exceptions;

import com.financial.system.security.context.jwt.enums.TokenStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class TokenExpiredException extends RuntimeException {
    private final TokenStatus tokenStatus = TokenStatus.EXPIRED;
    private final HttpStatusCode httpStatusCode = HttpStatus.CONFLICT;

    public TokenExpiredException(String message) {
        super(message);
    }
}
