package com.financialapp.users.domain.model.valueObject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InactivityPolicyTest {

    @Test
    void shouldAcceptAllowedMinutes() {
        assertThat(InactivityPolicy.fromMinutes(30).toMinutes()).isEqualTo(30);
        assertThat(InactivityPolicy.fromMinutes(120).toMinutes()).isEqualTo(120);
        assertThat(InactivityPolicy.fromMinutes(480).toMinutes()).isEqualTo(480);
        assertThat(InactivityPolicy.fromMinutes(-1).toMinutes()).isEqualTo(-1);
    }

    @Test
    void shouldRejectInvalidMinutes() {
        assertThatThrownBy(() -> InactivityPolicy.fromMinutes(15))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InactivityPolicy.fromMinutes(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InactivityPolicy.fromMinutes(60))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCheckExceededCorrectly() {
        InactivityPolicy policy30 = InactivityPolicy.fromMinutes(30);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSeen29MinAgo = now.minusMinutes(29);
        LocalDateTime lastSeen31MinAgo = now.minusMinutes(31);

        assertThat(policy30.exceededBy(lastSeen29MinAgo, now)).isFalse();
        assertThat(policy30.exceededBy(lastSeen31MinAgo, now)).isTrue();
    }

    @Test
    void shouldNeverExceedForNeverPolicy() {
        InactivityPolicy neverPolicy = InactivityPolicy.fromMinutes(-1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSeenDaysAgo = now.minusDays(10);

        assertThat(neverPolicy.exceededBy(lastSeenDaysAgo, now)).isFalse();
    }
}
