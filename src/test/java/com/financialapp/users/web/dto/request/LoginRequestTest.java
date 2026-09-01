package com.financialapp.users.web.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void accessors_returnConstructedValues() {
        LoginRequest req = new LoginRequest("a@b.com", "pass", true);
        assertThat(req.email()).isEqualTo("a@b.com");
        assertThat(req.password()).isEqualTo("pass");
        assertThat(req.isRememberMe()).isTrue();
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new LoginRequest("a@b.com", "p", false)).isEqualTo(new LoginRequest("a@b.com", "p", false));
    }

    @Test
    void equals_falseForDifferentEmail() {
        assertThat(new LoginRequest("a@b.com", "p", false)).isNotEqualTo(new LoginRequest("x@y.com", "p", false));
    }

    @Test
    void toString_containsEmail() {
        assertThat(new LoginRequest("a@b.com", "p", false).toString()).contains("a@b.com");
    }
}
