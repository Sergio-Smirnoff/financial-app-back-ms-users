package com.financialapp.users.infrastructure.persistence.jpa;

import com.financialapp.users.infrastructure.persistence.entity.ManualCurrencyRateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManualCurrencyRateJpaRepository extends JpaRepository<ManualCurrencyRateJpaEntity, Long> {
    List<ManualCurrencyRateJpaEntity> findByUserId(Long userId);
    Optional<ManualCurrencyRateJpaEntity> findByUserIdAndCurrency(Long userId, String currency);
    void deleteByUserIdAndCurrency(Long userId, String currency);
}
