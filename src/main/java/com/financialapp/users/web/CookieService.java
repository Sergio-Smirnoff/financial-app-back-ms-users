package com.financialapp.users.web;

import com.financialapp.users.domain.model.Session;
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
                         @Value("${jwt.expiration:86400000}") long jwtExpiration,
                         @Value("${jwt.refresh-expiration:604800000}") long jwtRefreshExpiration) {
        this.secure = secure;
        this.accessTokenMaxAge = jwtExpiration / 1000;
        this.refreshTokenMaxAge = jwtRefreshExpiration / 1000;
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true).secure(secure).sameSite("Lax")
                .path("/api").maxAge(Duration.ofSeconds(accessTokenMaxAge)).build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true).secure(secure).sameSite("Lax")
                .path("/api/v1/auth/refresh").maxAge(Duration.ofSeconds(refreshTokenMaxAge)).build();
    }

    public ResponseCookie createUserInfoCookie(Session session) {
        var user = session.user();
        String value = URLEncoder.encode(
                user.id().value() + "|" + user.email() + "|" + user.firstName() + "+" + user.lastName(),
                StandardCharsets.UTF_8
        );
        return ResponseCookie.from("user_info", value)
                .httpOnly(false).secure(secure).sameSite("Lax")
                .path("/").maxAge(Duration.ofSeconds(accessTokenMaxAge)).build();
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
