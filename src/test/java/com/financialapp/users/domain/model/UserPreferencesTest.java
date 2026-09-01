package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.InactivityPolicy;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPreferencesTest {

    @Test
    void shouldCreateDefaultPreferences() {
        UserId userId = new UserId(1L);
        UserPreferences defaults = UserPreferences.defaults(userId);

        assertThat(defaults.userId()).isEqualTo(userId);
        assertThat(defaults.inactivityPolicy().toMinutes()).isEqualTo(30);
        assertThat(defaults.primaryCurrency()).isEqualTo("ARS");
        assertThat(defaults.secondaryCurrency()).isNull();
        assertThat(defaults.numberFormat()).isEqualTo("es-AR");
        assertThat(defaults.decimals()).isEqualTo(2);
        assertThat(defaults.colorForAmounts()).isTrue();
    }

    @Test
    void shouldRejectInvalidPrimaryCurrency() {
        assertThatThrownBy(() -> new UserPreferences(
                new UserId(1L), InactivityPolicy.fromMinutes(30), "UTC", "INVALID", null, "es-AR", 2, true
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectInvalidSecondaryCurrency() {
        assertThatThrownBy(() -> new UserPreferences(
                new UserId(1L), InactivityPolicy.fromMinutes(30), "UTC", "ARS", "MEP", "es-AR", 2, true
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldAcceptValidSecondaryCurrencies() {
        UserPreferences p1 = new UserPreferences(
                new UserId(1L), InactivityPolicy.fromMinutes(30), "UTC", "ARS", "USD_MEP", "es-AR", 2, true
        );
        assertThat(p1.secondaryCurrency()).isEqualTo("USD_MEP");
    }

    @Test
    void shouldRejectInvalidNumberFormat() {
        assertThatThrownBy(() -> new UserPreferences(
                new UserId(1L), InactivityPolicy.fromMinutes(30), "UTC", "ARS", null, "de-DE", 2, true
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectInvalidDecimals() {
        assertThatThrownBy(() -> new UserPreferences(
                new UserId(1L), InactivityPolicy.fromMinutes(30), "UTC", "ARS", null, "es-AR", 3, true
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
