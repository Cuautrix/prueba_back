package com.financial.system.shared.exceptions;

import com.financial.system.auth.exceptions.EmailAlreadyValidatedException;
import com.financial.system.auth.exceptions.EmailNoValidatedException;
import com.financial.system.auth.exceptions.TokenExpiredException;
import com.financial.system.auth.exceptions.TokenHasBeenUsedException;
import com.financial.system.security.exceptions.AccountLockedException;
import com.financial.system.security.exceptions.AccountLockedTemporarilyException;
import com.financial.system.shared.payload.FinancialSystemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FinancialSystemException.class)
    public ResponseEntity<FinancialSystemResponse> handleFinancialSystemException(FinancialSystemException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getHttpStatus())
                .code(ex.getHttpStatusCode().value())
                .build());
    }


    @ExceptionHandler(EmailAlreadyValidatedException.class)
    public ResponseEntity<FinancialSystemResponse> handleEmailAlreadyValidatedException(EmailAlreadyValidatedException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getFinancialSystemStatus())
                .code(ex.getHttpStatusCode().value())
                .build());
    }
    @ExceptionHandler(AccountLockedTemporarilyException.class)
    public ResponseEntity<FinancialSystemResponse> handleAccountLockedTemporarilyException(AccountLockedTemporarilyException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getStatus())
                .code(ex.getHttpStatus().value())
                .build());
    }
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<FinancialSystemResponse> handleAccountLockedException(AccountLockedException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getStatus())
                .code(ex.getHttpStatus().value())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FinancialSystemResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String errorMessage = bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .findFirst()
                .orElse("Error de validación desconocido");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FinancialSystemResponse.builder()
                .message(errorMessage)
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(EmailNoValidatedException.class)
    public ResponseEntity<FinancialSystemResponse> handleEmailNoValidatedException(EmailNoValidatedException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getFinancialSystemStatus())
                .code(ex.getHttpStatusCode().value())
                .build());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<FinancialSystemResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getTokenStatus())
                .code(ex.getHttpStatusCode().value())
                .build());
    }

    @ExceptionHandler(TokenHasBeenUsedException.class)
    public ResponseEntity<FinancialSystemResponse> handleTokenHasBeenUsedException(TokenHasBeenUsedException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(FinancialSystemResponse.builder()
                .message(ex.getMessage())
                .status(ex.getTokenStatus())
                .code(ex.getHttpStatusCode().value())
                .build());
    }
}
