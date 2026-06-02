package com.financialapp.users.infrastructure.gateway;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationProviderGatewayImplTest {

    private static final String SECRET = "test-secret-key-that-is-long-enough-for-hs256-signing";
    private AuthenticationProviderGatewayImpl gateway;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret(SECRET);
        props.setExpiration(3_600_000L);
        props.setRefreshExpiration(86_400_000L);
        gateway = new AuthenticationProviderGatewayImpl(props);
        signingKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private User user() {
        return new User(new UserId(7L), "a@b.com", "hashed", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void generateAuthenticationToken_containsUserIdAsSubject() {
        String token = gateway.generateAuthenticationToken(user());
        String subject = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload().getSubject();
        assertThat(subject).isEqualTo("7");
    }

    @Test
    void generateAuthenticationToken_containsEmailClaim() {
        String token = gateway.generateAuthenticationToken(user());
        String email = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload().get("email", String.class);
        assertThat(email).isEqualTo("a@b.com");
    }

    @Test
    void refreshAuthenticationToken_containsUserIdAsSubject() {
        String token = gateway.refreshAuthenticationToken(user());
        String subject = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload().getSubject();
        assertThat(subject).isEqualTo("7");
    }

    @Test
    void refreshAuthenticationToken_containsRefreshTypeClaim() {
        String token = gateway.refreshAuthenticationToken(user());
        String type = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload().get("type", String.class);
        assertThat(type).isEqualTo("refresh");
    }

    @Test
    void getUserId_returnsCorrectUserId_fromValidToken() {
        String token = gateway.generateAuthenticationToken(user());
        UserId userId = gateway.getUserId(token);
        assertThat(userId).isEqualTo(new UserId(7L));
    }

    @Test
    void getUserId_throwsJwtException_forInvalidToken() {
        assertThatThrownBy(() -> gateway.getUserId("not.a.valid.token"))
                .isInstanceOf(JwtException.class);
    }
}
