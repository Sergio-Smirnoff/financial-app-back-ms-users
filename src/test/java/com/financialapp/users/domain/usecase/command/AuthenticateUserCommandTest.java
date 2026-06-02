package com.financialapp.users.domain.usecase.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticateUserCommandTest {

    @Test
    void accessors_returnConstructedValues() {
        AuthenticateUserCommand cmd = new AuthenticateUserCommand("a@b.com", "pass");
        assertThat(cmd.email()).isEqualTo("a@b.com");
        assertThat(cmd.password()).isEqualTo("pass");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new AuthenticateUserCommand("a@b.com", "p"))
                .isEqualTo(new AuthenticateUserCommand("a@b.com", "p"));
    }

    @Test
    void equals_falseForDifferentPassword() {
        assertThat(new AuthenticateUserCommand("a@b.com", "p1"))
                .isNotEqualTo(new AuthenticateUserCommand("a@b.com", "p2"));
    }

    @Test
    void toString_containsEmail() {
        assertThat(new AuthenticateUserCommand("a@b.com", "p").toString()).contains("a@b.com");
    }
}
