package com.financialapp.users.infrastructure.gateway;

import com.financialapp.users.domain.exception.InvalidTokenException;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.RefreshTokenClaims;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationProviderGatewayImplTest {

    private static final String SECRET = "0123456789012345678901234567890123456789";
    private JwtProperties jwtProperties;
    private AuthenticationProviderGatewayImpl authProvider;
    private SecretKey signingKey;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(SECRET);
        jwtProperties.setExpiration(86400000L); // 24h
        jwtProperties.setRefreshExpiration(604800000L); // 7d

        authProvider = new AuthenticationProviderGatewayImpl(jwtProperties);
        signingKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        sampleUser = new User(new UserId(42L), "user@example.com", "hash", "John", "Doe", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldRejectAccessTokenPresentedToRefreshPath() {
        String accessToken = authProvider.generateAuthenticationToken(sampleUser, new SessionId(100L));

        assertThatThrownBy(() -> authProvider.getRefreshTokenClaims(accessToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Expected refresh token type");
    }

    @Test
    void shouldRejectRefreshTokenPresentedToAccessPath() {
        String refreshToken = authProvider.refreshAuthenticationToken(sampleUser, RefreshTokenId.generate(), false);

        assertThatThrownBy(() -> authProvider.getUserIdFromAccessToken(refreshToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Expected access token type");
    }

    @Test
    void shouldVerifyWellFormedAccessTokenAndReturnUserId() {
        String accessToken = authProvider.generateAuthenticationToken(sampleUser, new SessionId(100L));

        UserId userId = authProvider.getUserIdFromAccessToken(accessToken);

        assertThat(userId).isEqualTo(new UserId(42L));
    }

    @Test
    void shouldRejectTokenWithNoTypeClaimOnBothPaths() {
        String tokenWithoutType = Jwts.builder()
                .subject("42")
                .claim("email", "user@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(signingKey)
                .compact();

        assertThatThrownBy(() -> authProvider.getUserIdFromAccessToken(tokenWithoutType))
                .isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(() -> authProvider.getRefreshTokenClaims(tokenWithoutType))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldMintAccessTokenWithExactClaimSet() {
        SessionId sessionId = new SessionId(123L);
        String accessToken = authProvider.generateAuthenticationToken(sampleUser, sessionId);

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        Set<String> claimKeys = claims.keySet();
        assertThat(claimKeys).containsExactlyInAnyOrder("sub", "email", "firstName", "type", "sid", "iat", "exp");
        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("email")).isEqualTo("user@example.com");
        assertThat(claims.get("firstName")).isEqualTo("John");
        assertThat(claims.get("type")).isEqualTo("access");
        assertThat(claims.get("sid", Long.class)).isEqualTo(123L);
    }
}
