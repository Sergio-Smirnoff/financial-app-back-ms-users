package com.financialapp.users.web.error;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.commons.web.error.ApiExceptionHandler;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ApiExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
        log.warn("JWT error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.failure(
                HttpStatus.UNAUTHORIZED, "invalid_token", "Invalid or expired token", null));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingCookie(MissingRequestCookieException ex) {
        log.warn("Missing cookie: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.failure(
                HttpStatus.UNAUTHORIZED, "authentication_required", "Authentication required", null));
    }
}
