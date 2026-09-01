package com.financialapp.users.web.dto.request;

public record UpdateUserPreferencesRequest(
        Integer maxIdleMinutes,
        String timezone,
        String primaryCurrency,
        String secondaryCurrency,
        String numberFormat,
        Integer decimals,
        Boolean colorForAmounts
) {
}
