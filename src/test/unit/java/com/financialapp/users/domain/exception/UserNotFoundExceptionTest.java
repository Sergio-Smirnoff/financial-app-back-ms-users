package com.financialapp.users.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    void message_matchesConstructorArg() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        assertThat(ex.getMessage()).isEqualTo("User not found");
    }

    @Test
    void isRuntimeException() {
        assertThat(new UserNotFoundException("msg")).isInstanceOf(RuntimeException.class);
    }
}
