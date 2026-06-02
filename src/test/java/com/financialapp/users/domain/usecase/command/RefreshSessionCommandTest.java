package com.financialapp.users.domain.usecase.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshSessionCommandTest {

    @Test
    void accessor_returnsToken() {
        RefreshSessionCommand cmd = new RefreshSessionCommand("my-refresh-token");
        assertThat(cmd.refreshAuthentication()).isEqualTo("my-refresh-token");
    }

    @Test
    void equals_trueForSameToken() {
        assertThat(new RefreshSessionCommand("tok")).isEqualTo(new RefreshSessionCommand("tok"));
    }

    @Test
    void equals_falseForDifferentToken() {
        assertThat(new RefreshSessionCommand("a")).isNotEqualTo(new RefreshSessionCommand("b"));
    }

    @Test
    void toString_containsToken() {
        assertThat(new RefreshSessionCommand("tok").toString()).contains("tok");
    }
}
