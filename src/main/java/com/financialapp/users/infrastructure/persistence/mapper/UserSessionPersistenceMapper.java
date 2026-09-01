package com.financialapp.users.infrastructure.persistence.mapper;

import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.UserSessionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserSessionPersistenceMapper {

    public UserSession toDomain(UserSessionJpaEntity entity) {
        if (entity == null) return null;
        return new UserSession(
                entity.getId() != null ? new SessionId(entity.getId()) : null,
                new UserId(entity.getUserId()),
                new RefreshTokenId(entity.getRefreshTokenId()),
                new DeviceLabel(entity.getDevice()),
                entity.isRememberMe(),
                entity.getCreatedAt(),
                entity.getLastSeenAt(),
                entity.isRevoked()
        );
    }

    public UserSessionJpaEntity toJpa(UserSession domain) {
        if (domain == null) return null;
        return UserSessionJpaEntity.builder()
                .id(domain.id() != null ? domain.id().value() : null)
                .userId(domain.userId().value())
                .refreshTokenId(domain.refreshTokenId().value())
                .device(domain.device().value())
                .rememberMe(domain.rememberMe())
                .createdAt(domain.createdAt())
                .lastSeenAt(domain.lastSeenAt())
                .revoked(domain.revoked())
                .build();
    }
}
