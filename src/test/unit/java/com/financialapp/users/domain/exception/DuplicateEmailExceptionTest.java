package com.financialapp.users.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateEmailExceptionTest {

    @Test
    void message_containsEmail() {
        DuplicateEmailException ex = new DuplicateEmailException("user@example.com");
        assertThat(ex.getMessage()).isEqualTo("Email already registered: user@example.com");
    }

    @Test
    void isRuntimeException() {
        assertThat(new DuplicateEmailException("x@y.com")).isInstanceOf(RuntimeException.class);
    }
}
