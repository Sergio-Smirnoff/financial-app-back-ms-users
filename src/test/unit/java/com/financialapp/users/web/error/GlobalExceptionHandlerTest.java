package com.financialapp.users.web.error;

import com.financialapp.users.domain.exception.DuplicateEmailException;
import com.financialapp.users.domain.exception.InvalidCredentialsException;
import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.web.dto.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDuplicateEmail_returns409WithMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleDuplicateEmail(new DuplicateEmailException("a@b.com"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("a@b.com");
    }

    @Test
    void handleInvalidCredentials_returns401() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleInvalidCredentials(new InvalidCredentialsException());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid email or password");
    }

    @Test
    void handleUserNotFound_returns404() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleUserNotFound(new UserNotFoundException("User not found"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void handleJwtException_returns401() {
        JwtException ex = new MalformedJwtException("bad jwt");
        ResponseEntity<ApiResponse<Void>> response = handler.handleJwtException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid or expired token");
    }

    @Test
    void handleMissingCookie_returns401() {
        MissingRequestCookieException ex = mock(MissingRequestCookieException.class);
        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingCookie(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication required");
    }

    @Test
    void handleValidation_returns400WithFieldErrors() {
        FieldError fieldError = new FieldError("req", "email", "Email is required");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getErrors()).containsExactly("Email is required");
    }

    @Test
    void handleGeneric_returns500() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleGeneric(new RuntimeException("boom"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}
