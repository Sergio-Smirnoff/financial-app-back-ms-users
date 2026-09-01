package com.financialapp.users.domain.model.valueObject;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenIdTest {

    @Test
    void shouldGenerateNonNullUuid() {
        RefreshTokenId id = RefreshTokenId.generate();
        assertThat(id.value()).isNotNull();
    }

    @Test
    void shouldRejectNullValue() {
        assertThatThrownBy(() -> new RefreshTokenId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldMaintainValue() {
        UUID uuid = UUID.randomUUID();
        RefreshTokenId id = new RefreshTokenId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }
}
