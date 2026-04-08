package com.financialapp.users.controller;

import com.financialapp.users.model.dto.request.LoginRequest;
import com.financialapp.users.model.dto.request.RegisterRequest;
import com.financialapp.users.model.dto.response.ApiResponse;
import com.financialapp.users.model.dto.response.AuthResponse;
import com.financialapp.users.model.entity.User;
import com.financialapp.users.service.AuthService;
import com.financialapp.users.service.CookieService;
import com.financialapp.users.service.JwtService;
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

    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return buildAuthResponse(user, HttpStatus.CREATED, "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return buildAuthResponse(user, HttpStatus.OK, "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refresh_token") String refreshToken) {
        User user = authService.refreshToken(refreshToken, jwtService);
        return buildAuthResponse(user, HttpStatus.OK, "Token refreshed");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        HttpHeaders headers = new HttpHeaders();
        cookieService.createLogoutCookies()
                .forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.ok("Logged out successfully", null));
    }

    private ResponseEntity<ApiResponse<AuthResponse>> buildAuthResponse(User user, HttpStatus status, String message) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(accessToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(refreshToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieService.createUserInfoCookie(user).toString());

        AuthResponse authResponse = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(ApiResponse.ok(message, authResponse));
    }
}
