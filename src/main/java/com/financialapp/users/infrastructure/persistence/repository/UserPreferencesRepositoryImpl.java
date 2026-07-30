package com.financialapp.users.infrastructure.persistence.repository;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserPreferencesRepository;
import com.financialapp.users.infrastructure.persistence.jpa.UserPreferencesJpaRepository;
import com.financialapp.users.infrastructure.persistence.mapper.UserPreferencesPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPreferencesRepositoryImpl implements UserPreferencesRepository {

    private final UserPreferencesJpaRepository jpaRepository;
    private final UserPreferencesPersistenceMapper mapper;

    @Override
    public UserPreferences findByUser(UserId userId) {
        return jpaRepository.findById(userId.value())
                .map(mapper::toDomain)
                .orElseGet(() -> UserPreferences.defaults(userId));
    }

    @Override
    public UserPreferences save(UserPreferences preferences) {
        var entity = mapper.toJpa(preferences);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
