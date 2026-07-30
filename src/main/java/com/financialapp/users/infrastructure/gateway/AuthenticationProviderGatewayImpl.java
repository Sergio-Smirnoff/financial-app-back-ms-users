package com.financialapp.users.infrastructure.gateway;

import com.financialapp.users.domain.exception.InvalidTokenException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.RefreshTokenClaims;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class AuthenticationProviderGatewayImpl implements AuthenticationProviderGateway {

    private static final long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000;

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public AuthenticationProviderGatewayImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAuthenticationToken(User user, SessionId sessionId) {
        var builder = Jwts.builder()
                .subject(user.id().value().toString())
                .claim("email", user.email())
                .claim("firstName", user.firstName())
                .claim("type", "access");

        if (sessionId != null && sessionId.value() != null) {
            builder.claim("sid", sessionId.value());
        }

        return builder
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String refreshAuthenticationToken(User user, RefreshTokenId jti, boolean rememberMe) {
        long ttlMs = rememberMe ? THIRTY_DAYS_MS : jwtProperties.getRefreshExpiration();
        var builder = Jwts.builder()
                .subject(user.id().value().toString())
                .claim("type", "refresh");

        if (jti != null && jti.value() != null) {
            builder.id(jti.value().toString());
        }

        return builder
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMs))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public UserId getUserIdFromAccessToken(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get("type", String.class);
        if (!"access".equals(type)) {
            throw new InvalidTokenException("Expected access token type but was: " + type);
        }

        try {
            return new UserId(Long.parseLong(claims.getSubject()));
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid subject in access token");
        }
    }

    @Override
    public Long getSessionIdFromAccessToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            Claims claims = parseClaims(token);
            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                return null;
            }
            return claims.get("sid", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public RefreshTokenClaims getRefreshTokenClaims(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new InvalidTokenException("Expected refresh token type but was: " + type);
        }

        String jtiStr = claims.getId();
        if (jtiStr == null || jtiStr.isBlank()) {
            jtiStr = claims.get("jti", String.class);
        }
        if (jtiStr == null || jtiStr.isBlank()) {
            throw new InvalidTokenException("Refresh token missing jti claim");
        }

        try {
            UserId userId = new UserId(Long.parseLong(claims.getSubject()));
            RefreshTokenId refreshTokenId = new RefreshTokenId(UUID.fromString(jtiStr));
            return new RefreshTokenClaims(userId, refreshTokenId);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid subject or jti in refresh token");
        }
    }

    private Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token is null or empty");
        }
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new InvalidTokenException("Token verification failed: " + e.getMessage());
        }
    }
}
