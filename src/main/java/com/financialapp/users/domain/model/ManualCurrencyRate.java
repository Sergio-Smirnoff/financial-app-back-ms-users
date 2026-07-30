package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ManualCurrencyRate(
        UserId userId,
        String currency,
        BigDecimal ratePerArs,
        LocalDateTime updatedAt
) {
    public ManualCurrencyRate {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency cannot be blank");
        }
        String upperCurrency = currency.trim().toUpperCase();
        if ("ARS".equals(upperCurrency) || "USD".equals(upperCurrency)) {
            throw new IllegalArgumentException("Manual currency rate cannot be set for ARS or USD");
        }
        try {
            java.util.Currency.getInstance(upperCurrency);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid currency code: " + currency);
        }
        if (ratePerArs == null || ratePerArs.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("ratePerArs must be strictly positive");
        }
        currency = upperCurrency;
    }
}
