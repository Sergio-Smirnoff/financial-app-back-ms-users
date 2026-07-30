package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserSessionTest {

    @Test
    void shouldTouchAndUpdateLastSeenAt() {
        LocalDateTime now = LocalDateTime.now();
        UserSession session = new UserSession(
                new SessionId(1L),
                new UserId(10L),
                RefreshTokenId.generate(),
                new DeviceLabel("Chrome · Linux"),
                false,
                now.minusDays(1),
                now.minusDays(1),
                false
        );

        LocalDateTime updatedTime = LocalDateTime.now();
        UserSession touched = session.touch(updatedTime);

        assertThat(touched.lastSeenAt()).isEqualTo(updatedTime);
        assertThat(touched.createdAt()).isEqualTo(session.createdAt());
        assertThat(touched.revoked()).isFalse();
    }

    @Test
    void shouldRevokeSessionIdempotently() {
        LocalDateTime now = LocalDateTime.now();
        UserSession session = new UserSession(
                new SessionId(1L),
                new UserId(10L),
                RefreshTokenId.generate(),
                new DeviceLabel("Chrome · Linux"),
                false,
                now,
                now,
                false
        );

        UserSession revoked = session.revoke();
        assertThat(revoked.revoked()).isTrue();

        UserSession revokedAgain = revoked.revoke();
        assertThat(revokedAgain).isEqualTo(revoked);
    }

    @Test
    void shouldRotateRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        RefreshTokenId oldId = RefreshTokenId.generate();
        UserSession session = new UserSession(
                new SessionId(1L),
                new UserId(10L),
                oldId,
                new DeviceLabel("Chrome · Linux"),
                false,
                now,
                now,
                false
        );

        RefreshTokenId newId = RefreshTokenId.generate();
        UserSession rotated = session.withRotatedToken(newId);

        assertThat(rotated.refreshTokenId()).isEqualTo(newId);
        assertThat(rotated.id()).isEqualTo(session.id());
    }
}
