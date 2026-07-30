package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.InactivityPolicy;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.util.Currency;
import java.util.Objects;
import java.util.Set;

public record UserPreferences(
        UserId userId,
        InactivityPolicy inactivityPolicy,
        String timezone,
        String primaryCurrency,
        String secondaryCurrency,
        String numberFormat,
        int decimals,
        boolean colorForAmounts
) {
    private static final Set<String> ALLOWED_SECONDARY_CURRENCIES = Set.of("USD_MEP", "USD_CCL", "USD_OFICIAL");
    private static final Set<String> ALLOWED_NUMBER_FORMATS = Set.of("es-AR", "en-US");

    public UserPreferences {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(inactivityPolicy, "inactivityPolicy cannot be null");
        Objects.requireNonNull(timezone, "timezone cannot be null");
        Objects.requireNonNull(primaryCurrency, "primaryCurrency cannot be null");
        Objects.requireNonNull(numberFormat, "numberFormat cannot be null");

        try {
            Currency.getInstance(primaryCurrency);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid primaryCurrency: " + primaryCurrency);
        }

        if (secondaryCurrency != null && !ALLOWED_SECONDARY_CURRENCIES.contains(secondaryCurrency)) {
            throw new IllegalArgumentException("Invalid secondaryCurrency: " + secondaryCurrency + ". Allowed: " + ALLOWED_SECONDARY_CURRENCIES);
        }

        if (!ALLOWED_NUMBER_FORMATS.contains(numberFormat)) {
            throw new IllegalArgumentException("Invalid numberFormat: " + numberFormat + ". Allowed: " + ALLOWED_NUMBER_FORMATS);
        }

        if (decimals != 0 && decimals != 2) {
            throw new IllegalArgumentException("Invalid decimals: " + decimals + ". Allowed: 0 or 2");
        }
    }

    public static UserPreferences defaults(UserId userId) {
        return new UserPreferences(
                userId,
                InactivityPolicy.fromMinutes(30),
                "America/Argentina/Buenos_Aires",
                "ARS",
                null,
                "es-AR",
                2,
                true
        );
    }
}
