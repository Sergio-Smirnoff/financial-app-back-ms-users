package com.financialapp.users.web.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void accessors_returnConstructedValues() {
        LoginRequest req = new LoginRequest("a@b.com", "pass");
        assertThat(req.email()).isEqualTo("a@b.com");
        assertThat(req.password()).isEqualTo("pass");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new LoginRequest("a@b.com", "p")).isEqualTo(new LoginRequest("a@b.com", "p"));
    }

    @Test
    void equals_falseForDifferentEmail() {
        assertThat(new LoginRequest("a@b.com", "p")).isNotEqualTo(new LoginRequest("x@y.com", "p"));
    }

    @Test
    void toString_containsEmail() {
        assertThat(new LoginRequest("a@b.com", "p").toString()).contains("a@b.com");
    }
}
