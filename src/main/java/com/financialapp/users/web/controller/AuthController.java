package com.financialapp.users.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.commons.web.openapi.ApiErrorCodes;
import com.financialapp.users.domain.exception.DomainError;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.RefreshTokenClaims;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.AuthenticateUserUseCase;
import com.financialapp.users.domain.usecase.RefreshSessionUseCase;
import com.financialapp.users.domain.usecase.RegisterUserUseCase;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import com.financialapp.users.domain.usecase.command.RegisterUserCommand;
import com.financialapp.users.web.CookieService;
import com.financialapp.users.web.dto.request.LoginRequest;
import com.financialapp.users.web.dto.request.RegisterRequest;
import com.financialapp.users.web.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUseCase;
    private final AuthenticateUserUseCase authenticateUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final UserSessionRepository userSessionRepository;
    private final AuthenticationProviderGateway authProvider;
    private final CookieService cookieService;

    @PostMapping("/register")
    @ApiErrorCodes(catalog = DomainError.class, value = {"email_already_registered"})
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        Session session = registerUseCase.execute(
                new RegisterUserCommand(request.email(), request.password(), request.firstName(), request.lastName(), request.isRememberMe(), userAgent)
        );
        return buildAuthResponse(session, HttpStatus.CREATED, "User registered successfully", request.isRememberMe());
    }

    @PostMapping("/login")
    @ApiErrorCodes(catalog = DomainError.class, value = {"invalid_credentials"})
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        Session session = authenticateUseCase.execute(
                new AuthenticateUserCommand(request.email(), request.password(), request.isRememberMe(), userAgent)
        );
        return buildAuthResponse(session, HttpStatus.OK, "Login successful", request.isRememberMe());
    }

    @PostMapping("/refresh")
    @ApiErrorCodes(catalog = DomainError.class, value = {"user_not_found", "invalid_token", "authentication_required"})
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refresh_token") String refreshToken,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        Session session = refreshSessionUseCase.execute(new RefreshSessionCommand(refreshToken, userAgent));
        return buildAuthResponse(session, HttpStatus.OK, "Token refreshed", false);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                RefreshTokenClaims claims = authProvider.getRefreshTokenClaims(refreshToken);
                userSessionRepository.findByRefreshTokenId(claims.jti())
                        .ifPresent(s -> userSessionRepository.save(s.revoke()));
            } catch (Exception e) {
                // Ignore invalid token on logout, proceed to clear cookies
            }
        }
        HttpHeaders headers = new HttpHeaders();
        cookieService.createLogoutCookies()
                .forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()));
        return ResponseEntity.ok().headers(headers).body(ApiResponse.ok("Logged out successfully", null));
    }

    private ResponseEntity<ApiResponse<AuthResponse>> buildAuthResponse(Session session, HttpStatus status, String message, boolean rememberMe) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(session.accessAuthentication()).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(session.refreshAuthentication(), rememberMe).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createUserInfoCookie(session).toString());

        User user = session.user();
        AuthResponse authResponse = AuthResponse.builder()
                .userId(user.id().value())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .build();

        ApiResponse<AuthResponse> body = status == HttpStatus.CREATED
                ? ApiResponse.created(message, authResponse)
                : ApiResponse.ok(message, authResponse);
        return ResponseEntity.status(status).headers(headers).body(body);
    }
}
