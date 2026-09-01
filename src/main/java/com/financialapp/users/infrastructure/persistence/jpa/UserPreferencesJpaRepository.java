package com.financialapp.users.infrastructure.persistence.jpa;

import com.financialapp.users.infrastructure.persistence.entity.UserPreferencesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesJpaRepository extends JpaRepository<UserPreferencesJpaEntity, Long> {
}
