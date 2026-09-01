package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManualCurrencyRateTest {

    @Test
    void shouldRejectArsAndUsd() {
        UserId userId = new UserId(1L);
        BigDecimal rate = new BigDecimal("1350.50");
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> new ManualCurrencyRate(userId, "ARS", rate, now))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ManualCurrencyRate(userId, "USD", rate, now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectZeroOrNegativeRate() {
        UserId userId = new UserId(1L);
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> new ManualCurrencyRate(userId, "EUR", BigDecimal.ZERO, now))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new ManualCurrencyRate(userId, "EUR", new BigDecimal("-1.0"), now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldAcceptValidRateForEur() {
        UserId userId = new UserId(1L);
        BigDecimal rate = new BigDecimal("1350.50");
        LocalDateTime now = LocalDateTime.now();

        ManualCurrencyRate mcr = new ManualCurrencyRate(userId, "eur", rate, now);

        assertThat(mcr.currency()).isEqualTo("EUR");
        assertThat(mcr.ratePerArs()).isEqualTo(rate);
    }
}
