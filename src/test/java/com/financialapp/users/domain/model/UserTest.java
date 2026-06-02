package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 0, 0);

    private User user() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe", NOW, NOW);
    }

    @Test
    void accessors_returnConstructedValues() {
        User u = user();
        assertThat(u.id()).isEqualTo(new UserId(1L));
        assertThat(u.email()).isEqualTo("a@b.com");
        assertThat(u.password()).isEqualTo("hashed");
        assertThat(u.firstName()).isEqualTo("John");
        assertThat(u.lastName()).isEqualTo("Doe");
        assertThat(u.createdAt()).isEqualTo(NOW);
        assertThat(u.updatedAt()).isEqualTo(NOW);
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(user()).isEqualTo(user());
    }

    @Test
    void equals_falseForDifferentEmail() {
        User other = new User(new UserId(1L), "x@y.com", "hashed", "John", "Doe", NOW, NOW);
        assertThat(user()).isNotEqualTo(other);
    }

    @Test
    void hashCode_consistentWithEquals() {
        assertThat(user().hashCode()).isEqualTo(user().hashCode());
    }

    @Test
    void toString_containsEmail() {
        assertThat(user().toString()).contains("a@b.com");
    }
}
