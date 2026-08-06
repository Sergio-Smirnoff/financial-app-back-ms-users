package com.financialapp.users.web.dto.response;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileResponseTest {

    @Test
    void fromDomain_mapsUserWithFirstAndLastName() {
        User user = new User(
                new UserId(1L),
                "user@example.com",
                "hashed",
                "John",
                "Doe",
                LocalDateTime.of(2026, 8, 1, 12, 0),
                null
        );

        ProfileResponse response = ProfileResponse.fromDomain(user);

        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void fromDomain_handlesNullFields() {
        User user = new User(
                new UserId(2L),
                "nulls@example.com",
                "hashed",
                null,
                null,
                null,
                null
        );

        ProfileResponse response = ProfileResponse.fromDomain(user);

        assertThat(response.name()).isEqualTo("");
        assertThat(response.email()).isEqualTo("nulls@example.com");
        assertThat(response.createdAt()).isNull();
    }
}
