package com.financialapp.users.web.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    @Test
    void accessors_returnConstructedValues() {
        RegisterRequest req = new RegisterRequest("a@b.com", "pass12345", "John", "Doe");
        assertThat(req.email()).isEqualTo("a@b.com");
        assertThat(req.password()).isEqualTo("pass12345");
        assertThat(req.firstName()).isEqualTo("John");
        assertThat(req.lastName()).isEqualTo("Doe");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new RegisterRequest("a@b.com", "p", "J", "D"))
                .isEqualTo(new RegisterRequest("a@b.com", "p", "J", "D"));
    }

    @Test
    void equals_falseForDifferentFirstName() {
        assertThat(new RegisterRequest("a@b.com", "p", "John", "D"))
                .isNotEqualTo(new RegisterRequest("a@b.com", "p", "Jane", "D"));
    }

    @Test
    void toString_containsEmail() {
        assertThat(new RegisterRequest("a@b.com", "p", "J", "D").toString()).contains("a@b.com");
    }
}
