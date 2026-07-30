package com.financialapp.users.domain.usecase.command;

import com.financialapp.users.domain.model.valueObject.UserId;

import java.math.BigDecimal;

public record SetManualCurrencyRateCommand(
        UserId userId,
        String currency,
        BigDecimal ratePerArs
) {
}
