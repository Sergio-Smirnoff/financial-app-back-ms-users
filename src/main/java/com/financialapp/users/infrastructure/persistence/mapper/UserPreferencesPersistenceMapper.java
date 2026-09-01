package com.financialapp.users.infrastructure.persistence.mapper;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.InactivityPolicy;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.UserPreferencesJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesPersistenceMapper {

    public UserPreferences toDomain(UserPreferencesJpaEntity entity) {
        if (entity == null) return null;
        return new UserPreferences(
                new UserId(entity.getUserId()),
                InactivityPolicy.fromMinutes(entity.getMaxIdleMinutes()),
                entity.getTimezone(),
                entity.getPrimaryCurrency(),
                entity.getSecondaryCurrency(),
                entity.getNumberFormat(),
                entity.getDecimals(),
                entity.isColorForAmounts()
        );
    }

    public UserPreferencesJpaEntity toJpa(UserPreferences domain) {
        if (domain == null) return null;
        return UserPreferencesJpaEntity.builder()
                .userId(domain.userId().value())
                .maxIdleMinutes(domain.inactivityPolicy().toMinutes())
                .timezone(domain.timezone())
                .primaryCurrency(domain.primaryCurrency())
                .secondaryCurrency(domain.secondaryCurrency())
                .numberFormat(domain.numberFormat())
                .decimals(domain.decimals())
                .colorForAmounts(domain.colorForAmounts())
                .build();
    }
}
