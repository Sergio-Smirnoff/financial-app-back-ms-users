package com.financialapp.users.infrastructure.persistence.repository;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.ManualCurrencyRateRepository;
import com.financialapp.users.infrastructure.persistence.jpa.ManualCurrencyRateJpaRepository;
import com.financialapp.users.infrastructure.persistence.mapper.ManualCurrencyRatePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ManualCurrencyRateRepositoryImpl implements ManualCurrencyRateRepository {

    private final ManualCurrencyRateJpaRepository jpaRepository;
    private final ManualCurrencyRatePersistenceMapper mapper;

    @Override
    public ManualCurrencyRate save(ManualCurrencyRate rate) {
        var existing = jpaRepository.findByUserIdAndCurrency(rate.userId().value(), rate.currency());
        Long existingId = existing.map(e -> e.getId()).orElse(null);
        var entity = mapper.toJpa(rate, existingId);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<ManualCurrencyRate> findByUser(UserId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ManualCurrencyRate> findByUserAndCurrency(UserId userId, String currency) {
        return jpaRepository.findByUserIdAndCurrency(userId.value(), currency.toUpperCase())
                .map(mapper::toDomain);
    }

    @Override
    public boolean deleteByUserAndCurrency(UserId userId, String currency) {
        var existing = jpaRepository.findByUserIdAndCurrency(userId.value(), currency.toUpperCase());
        if (existing.isPresent()) {
            jpaRepository.delete(existing.get());
            return true;
        }
        return false;
    }
}
