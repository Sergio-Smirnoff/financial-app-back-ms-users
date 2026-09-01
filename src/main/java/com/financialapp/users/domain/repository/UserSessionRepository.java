package com.financialapp.users.domain.repository;

import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository {
    UserSession save(UserSession session);
    Optional<UserSession> findByRefreshTokenId(RefreshTokenId refreshTokenId);
    List<UserSession> findByUser(UserId userId);
    Optional<UserSession> findByIdOwnedBy(SessionId id, UserId userId);
}
