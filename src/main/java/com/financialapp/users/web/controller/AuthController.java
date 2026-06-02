package com.financialapp.users.web.controller;

import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.usecase.AuthenticateUserUseCase;
import com.financialapp.users.domain.usecase.RefreshSessionUseCase;
import com.financialapp.users.domain.usecase.RegisterUserUseCase;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import com.financialapp.users.domain.usecase.command.RegisterUserCommand;
import com.financialapp.users.web.CookieService;
import com.financialapp.users.web.dto.request.LoginRequest;
import com.financialapp.users.web.dto.request.RegisterRequest;
import com.financialapp.users.web.dto.response.ApiResponse;
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
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Session session = registerUseCase.execute(
                new RegisterUserCommand(request.email(), request.password(), request.firstName(), request.lastName())
        );
        return buildAuthResponse(session, HttpStatus.CREATED, "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        Session session = authenticateUseCase.execute(
                new AuthenticateUserCommand(request.email(), request.password())
        );
        return buildAuthResponse(session, HttpStatus.OK, "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refresh_token") String refreshToken) {
        Session session = refreshSessionUseCase.execute(new RefreshSessionCommand(refreshToken));
        return buildAuthResponse(session, HttpStatus.OK, "Token refreshed");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        HttpHeaders headers = new HttpHeaders();
        cookieService.createLogoutCookies()
                .forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()));
        return ResponseEntity.ok().headers(headers).body(ApiResponse.ok("Logged out successfully", null));
    }

    private ResponseEntity<ApiResponse<AuthResponse>> buildAuthResponse(Session session, HttpStatus status, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(session.accessAuthentication()).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(session.refreshAuthentication()).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createUserInfoCookie(session).toString());

        User user = session.user();
        AuthResponse authResponse = AuthResponse.builder()
                .userId(user.id().value())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .build();

        return ResponseEntity.status(status).headers(headers).body(ApiResponse.ok(message, authResponse));
    }
}
