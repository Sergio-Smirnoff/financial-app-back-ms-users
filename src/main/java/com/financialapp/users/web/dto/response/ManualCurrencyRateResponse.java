package com.financialapp.users.web.dto.response;

import com.financialapp.users.domain.model.ManualCurrencyRate;

import java.time.LocalDateTime;

public record ManualCurrencyRateResponse(
        String currency,
        String ratePerArs,
        LocalDateTime updatedAt
) {
    public static ManualCurrencyRateResponse fromDomain(ManualCurrencyRate rate) {
        return new ManualCurrencyRateResponse(
                rate.currency(),
                rate.ratePerArs().toPlainString(),
                rate.updatedAt()
        );
    }
}
