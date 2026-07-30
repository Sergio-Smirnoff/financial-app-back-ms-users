package com.financialapp.users.web.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    @Test
    void accessors_returnConstructedValues() {
        RegisterRequest req = new RegisterRequest("a@b.com", "pass12345", "John", "Doe", false);
        assertThat(req.email()).isEqualTo("a@b.com");
        assertThat(req.password()).isEqualTo("pass12345");
        assertThat(req.firstName()).isEqualTo("John");
        assertThat(req.lastName()).isEqualTo("Doe");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new RegisterRequest("a@b.com", "p", "J", "D", false))
                .isEqualTo(new RegisterRequest("a@b.com", "p", "J", "D", false));
    }

    @Test
    void equals_falseForDifferentFirstName() {
        assertThat(new RegisterRequest("a@b.com", "p", "John", "D", false))
                .isNotEqualTo(new RegisterRequest("a@b.com", "p", "Jane", "D", false));
    }

    @Test
    void toString_containsEmail() {
        assertThat(new RegisterRequest("a@b.com", "p", "J", "D", false).toString()).contains("a@b.com");
    }
}
