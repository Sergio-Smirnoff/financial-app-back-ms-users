package com.financialapp.users.domain.model.valueObject;

import java.util.Objects;

public record RefreshTokenClaims(UserId userId, RefreshTokenId jti) {
    public RefreshTokenClaims {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(jti, "jti cannot be null");
    }
}
