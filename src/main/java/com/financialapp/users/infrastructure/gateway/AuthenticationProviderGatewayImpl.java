package com.financialapp.users.infrastructure.gateway;

import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class AuthenticationProviderGatewayImpl implements AuthenticationProviderGateway {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public AuthenticationProviderGatewayImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAuthenticationToken(User user) {
        return Jwts.builder()
                .subject(user.id().value().toString())
                .claim("email", user.email())
                .claim("firstName", user.firstName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String refreshAuthenticationToken(User user) {
        return Jwts.builder()
                .subject(user.id().value().toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public UserId getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new UserId(Long.parseLong(claims.getSubject()));
    }
}
