package com.financialapp.users.domain.repository;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.util.List;
import java.util.Optional;

public interface ManualCurrencyRateRepository {
    ManualCurrencyRate save(ManualCurrencyRate rate);
    List<ManualCurrencyRate> findByUser(UserId userId);
    Optional<ManualCurrencyRate> findByUserAndCurrency(UserId userId, String currency);
    boolean deleteByUserAndCurrency(UserId userId, String currency);
}
