package com.financialapp.users.infrastructure.persistence.mapper;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.ManualCurrencyRateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ManualCurrencyRatePersistenceMapper {

    public ManualCurrencyRate toDomain(ManualCurrencyRateJpaEntity entity) {
        if (entity == null) return null;
        return new ManualCurrencyRate(
                new UserId(entity.getUserId()),
                entity.getCurrency(),
                entity.getRatePerArs(),
                entity.getUpdatedAt()
        );
    }

    public ManualCurrencyRateJpaEntity toJpa(ManualCurrencyRate domain, Long existingId) {
        if (domain == null) return null;
        return ManualCurrencyRateJpaEntity.builder()
                .id(existingId)
                .userId(domain.userId().value())
                .currency(domain.currency())
                .ratePerArs(domain.ratePerArs())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
