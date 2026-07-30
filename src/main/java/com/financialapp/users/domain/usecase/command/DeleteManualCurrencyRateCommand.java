package com.financialapp.users.domain.usecase.command;

import com.financialapp.users.domain.model.valueObject.UserId;

public record DeleteManualCurrencyRateCommand(
        UserId userId,
        String currency
) {
}
