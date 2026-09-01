package com.financialapp.users.web;

import com.financialapp.users.domain.model.Session;
import com.financialapp.users.infrastructure.config.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
public class CookieService {

    private static final long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000;

    private final boolean secure;
    private final JwtProperties jwtProperties;

    public CookieService(@Value("${app.cookie.secure:false}") boolean secure,
                         JwtProperties jwtProperties) {
        this.secure = secure;
        this.jwtProperties = jwtProperties;
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        long maxAgeSec = jwtProperties.getExpiration() / 1000;
        return ResponseCookie.from("access_token", token)
                .httpOnly(true).secure(secure).sameSite("Lax")
                .path("/api").maxAge(Duration.ofSeconds(maxAgeSec)).build();
    }

    public ResponseCookie createRefreshTokenCookie(String token, boolean rememberMe) {
        long ttlMs = rememberMe ? THIRTY_DAYS_MS : jwtProperties.getRefreshExpiration();
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true).secure(secure).sameSite("Lax")
                .path("/api/v1/auth/refresh").maxAge(Duration.ofSeconds(ttlMs / 1000)).build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return createRefreshTokenCookie(token, false);
    }

    public ResponseCookie createUserInfoCookie(Session session) {
        var user = session.user();
        long maxAgeSec = jwtProperties.getExpiration() / 1000;
        String value = URLEncoder.encode(
                user.id().value() + "|" + user.email() + "|" + user.firstName() + "+" + user.lastName(),
                StandardCharsets.UTF_8
        );
        return ResponseCookie.from("user_info", value)
                .httpOnly(false).secure(secure).sameSite("Lax")
                .path("/").maxAge(Duration.ofSeconds(maxAgeSec)).build();
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
