package com.financialapp.users.domain.model.valueObject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserIdTest {

    @Test
    void accessor_returnsValue() {
        assertThat(new UserId(42L).value()).isEqualTo(42L);
    }

    @Test
    void equals_trueForSameValue() {
        assertThat(new UserId(1L)).isEqualTo(new UserId(1L));
    }

    @Test
    void equals_falseForDifferentValue() {
        assertThat(new UserId(1L)).isNotEqualTo(new UserId(2L));
    }

    @Test
    void hashCode_consistentWithEquals() {
        assertThat(new UserId(5L).hashCode()).isEqualTo(new UserId(5L).hashCode());
    }

    @Test
    void toString_containsValue() {
        assertThat(new UserId(7L).toString()).contains("7");
    }
}
