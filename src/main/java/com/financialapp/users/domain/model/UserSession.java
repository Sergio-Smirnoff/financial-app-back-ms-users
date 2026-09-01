package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public record UserSession(
        SessionId id,
        UserId userId,
        RefreshTokenId refreshTokenId,
        DeviceLabel device,
        boolean rememberMe,
        LocalDateTime createdAt,
        LocalDateTime lastSeenAt,
        boolean revoked
) {
    public UserSession {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(refreshTokenId, "refreshTokenId cannot be null");
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        Objects.requireNonNull(lastSeenAt, "lastSeenAt cannot be null");
    }

    public UserSession touch(LocalDateTime now) {
        return new UserSession(id, userId, refreshTokenId, device, rememberMe, createdAt, now, revoked);
    }

    public UserSession revoke() {
        if (revoked) {
            return this;
        }
        return new UserSession(id, userId, refreshTokenId, device, rememberMe, createdAt, lastSeenAt, true);
    }

    public UserSession withRotatedToken(RefreshTokenId newId) {
        return new UserSession(id, userId, newId, device, rememberMe, createdAt, lastSeenAt, revoked);
    }
}
