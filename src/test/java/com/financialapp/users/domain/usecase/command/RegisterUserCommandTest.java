package com.financialapp.users.domain.usecase.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterUserCommandTest {

    @Test
    void accessors_returnConstructedValues() {
        RegisterUserCommand cmd = new RegisterUserCommand("a@b.com", "pass", "John", "Doe");
        assertThat(cmd.email()).isEqualTo("a@b.com");
        assertThat(cmd.password()).isEqualTo("pass");
        assertThat(cmd.firstName()).isEqualTo("John");
        assertThat(cmd.lastName()).isEqualTo("Doe");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new RegisterUserCommand("a@b.com", "p", "J", "D"))
                .isEqualTo(new RegisterUserCommand("a@b.com", "p", "J", "D"));
    }

    @Test
    void equals_falseForDifferentEmail() {
        assertThat(new RegisterUserCommand("a@b.com", "p", "J", "D"))
                .isNotEqualTo(new RegisterUserCommand("x@y.com", "p", "J", "D"));
    }

    @Test
    void toString_containsEmail() {
        assertThat(new RegisterUserCommand("a@b.com", "p", "J", "D").toString()).contains("a@b.com");
    }
}
