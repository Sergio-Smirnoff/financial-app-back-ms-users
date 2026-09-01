package com.financialapp.users.infrastructure.persistence.repository;

import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.infrastructure.persistence.jpa.UserSessionJpaRepository;
import com.financialapp.users.infrastructure.persistence.mapper.UserSessionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserSessionRepositoryImpl implements UserSessionRepository {

    private final UserSessionJpaRepository jpaRepository;
    private final UserSessionPersistenceMapper mapper;

    @Override
    public UserSession save(UserSession session) {
        var entity = mapper.toJpa(session);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserSession> findByRefreshTokenId(RefreshTokenId refreshTokenId) {
        return jpaRepository.findByRefreshTokenId(refreshTokenId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<UserSession> findByUser(UserId userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserSession> findByIdOwnedBy(SessionId id, UserId userId) {
        return jpaRepository.findByIdAndUserId(id.value(), userId.value())
                .map(mapper::toDomain);
    }
}
