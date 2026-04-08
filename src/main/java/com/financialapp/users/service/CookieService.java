package com.financialapp.users.service;

import com.financialapp.users.config.JwtProperties;
import com.financialapp.users.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
public class CookieService {

    private final boolean secure;
    private final long accessTokenMaxAge;
    private final long refreshTokenMaxAge;

    public CookieService(@Value("${app.cookie.secure:false}") boolean secure,
                         JwtProperties jwtProperties) {
        this.secure = secure;
        this.accessTokenMaxAge = jwtProperties.getExpiration() / 1000;
        this.refreshTokenMaxAge = jwtProperties.getRefreshExpiration() / 1000;
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/api")
                .maxAge(Duration.ofSeconds(accessTokenMaxAge))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofSeconds(refreshTokenMaxAge))
                .build();
    }

    public ResponseCookie createUserInfoCookie(User user) {
        String value = URLEncoder.encode(
                user.getId() + "|" + user.getEmail() + "|" + user.getFirstName() + "+" + user.getLastName(),
                StandardCharsets.UTF_8
        );
        return ResponseCookie.from("user_info", value)
                .httpOnly(false)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(accessTokenMaxAge))
                .build();
    }

    public List<ResponseCookie> createLogoutCookies() {
        return List.of(
                ResponseCookie.from("access_token", "").httpOnly(true).secure(secure)
                        .sameSite("Lax").path("/api").maxAge(0).build(),
                ResponseCookie.from("refresh_token", "").httpOnly(true).secure(secure)
                        .sameSite("Lax").path("/api/v1/auth/refresh").maxAge(0).build(),
                ResponseCookie.from("user_info", "").httpOnly(false).secure(secure)
                        .sameSite("Lax").path("/").maxAge(0).build()
        );
    }
}
