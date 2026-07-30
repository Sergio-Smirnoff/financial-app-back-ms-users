package com.financialapp.users.infrastructure.persistence.jpa;

import com.financialapp.users.infrastructure.persistence.entity.UserSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionJpaRepository extends JpaRepository<UserSessionJpaEntity, Long> {
    Optional<UserSessionJpaEntity> findByRefreshTokenId(UUID refreshTokenId);
    List<UserSessionJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<UserSessionJpaEntity> findByIdAndUserId(Long id, Long userId);
}
