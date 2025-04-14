package com.financial.system.auth.exceptions;

import com.financial.system.security.context.jwt.enums.TokenStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class TokenHasBeenUsedException extends RuntimeException {
    private final TokenStatus tokenStatus = TokenStatus.USED;
    private final HttpStatusCode httpStatusCode = HttpStatus.CONFLICT;

    public TokenHasBeenUsedException(String message) {
        super(message);
    }
}
