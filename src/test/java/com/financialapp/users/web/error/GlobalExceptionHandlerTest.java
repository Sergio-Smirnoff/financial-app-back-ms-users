package com.financialapp.users.web.error;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.users.domain.exception.DuplicateEmailException;
import com.financialapp.users.domain.exception.InvalidCredentialsException;
import com.financialapp.users.domain.exception.UserNotFoundException;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void duplicateEmailMapsTo409WithCode() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                handler.handleDomain(new DuplicateEmailException("a@b.com"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getCode()).isEqualTo("email_already_registered");
        assertThat(response.getBody().getMessage()).contains("a@b.com");
    }

    @Test
    void invalidCredentialsMapsTo401WithCode() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                handler.handleDomain(new InvalidCredentialsException());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("invalid_credentials");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid email or password");
    }

    @Test
    void userNotFoundMapsTo404WithCode() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                handler.handleDomain(new UserNotFoundException("User not found"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("user_not_found");
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void handleJwtException_returns401() {
        JwtException ex = new MalformedJwtException("bad jwt");
        ResponseEntity<ApiResponse<Void>> response = handler.handleJwtException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("invalid_token");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid or expired token");
    }

    @Test
    void handleMissingCookie_returns401() {
        MissingRequestCookieException ex = mock(MissingRequestCookieException.class);
        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingCookie(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("authentication_required");
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication required");
    }

    @Test
    void handleValidation_returns400WithFieldMap() {
        FieldError fieldError = new FieldError("req", "email", "Email is required");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiResponse<Map<String, String>>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("validation_error");
        assertThat(response.getBody().getData()).containsEntry("email", "Email is required");
    }

    @Test
    void handleGeneric_returns500() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleGeneric(new RuntimeException("boom"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCode()).isEqualTo("internal_error");
    }
}
