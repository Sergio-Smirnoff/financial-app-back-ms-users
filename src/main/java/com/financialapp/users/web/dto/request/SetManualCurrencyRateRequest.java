package com.financialapp.users.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SetManualCurrencyRateRequest(
        @NotNull @Positive BigDecimal ratePerArs
) {
}
