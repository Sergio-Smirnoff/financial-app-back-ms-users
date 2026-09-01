package com.financialapp.users.domain.usecase.command;

import com.financialapp.users.domain.model.valueObject.UserId;

public record UpdateUserPreferencesCommand(
        UserId userId,
        Integer maxIdleMinutes,
        String timezone,
        String primaryCurrency,
        String secondaryCurrency,
        String numberFormat,
        Integer decimals,
        Boolean colorForAmounts
) {
}
