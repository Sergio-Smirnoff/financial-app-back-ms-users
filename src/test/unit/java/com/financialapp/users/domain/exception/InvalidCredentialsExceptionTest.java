package com.financialapp.users.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidCredentialsExceptionTest {

    @Test
    void message_isFixed() {
        assertThat(new InvalidCredentialsException().getMessage())
                .isEqualTo("Invalid email or password");
    }

    @Test
    void isRuntimeException() {
        assertThat(new InvalidCredentialsException()).isInstanceOf(RuntimeException.class);
    }
}
