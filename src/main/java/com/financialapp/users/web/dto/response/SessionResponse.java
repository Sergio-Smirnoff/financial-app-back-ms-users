package com.financialapp.users.web.dto.response;

import com.financialapp.users.domain.model.UserSession;

import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        String device,
        boolean current,
        boolean rememberMe,
        LocalDateTime createdAt,
        LocalDateTime lastSeenAt
) {
    public static SessionResponse fromDomain(UserSession session, boolean isCurrent) {
        return new SessionResponse(
                session.id() != null ? session.id().value() : null,
                session.device().value(),
                isCurrent,
                session.rememberMe(),
                session.createdAt(),
                session.lastSeenAt()
        );
    }
}
