package com.financialapp.users.web.dto.response;

import com.financialapp.users.domain.model.UserPreferences;

public record UserPreferencesResponse(
        int maxIdleMinutes,
        String timezone,
        String primaryCurrency,
        String secondaryCurrency,
        String numberFormat,
        int decimals,
        boolean colorForAmounts
) {
    public static UserPreferencesResponse fromDomain(UserPreferences prefs) {
        return new UserPreferencesResponse(
                prefs.inactivityPolicy().toMinutes(),
                prefs.timezone(),
                prefs.primaryCurrency(),
                prefs.secondaryCurrency(),
                prefs.numberFormat(),
                prefs.decimals(),
                prefs.colorForAmounts()
        );
    }
}
