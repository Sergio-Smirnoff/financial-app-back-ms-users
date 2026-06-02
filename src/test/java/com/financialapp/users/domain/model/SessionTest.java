package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 0, 0);

    private User user() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe", NOW, NOW);
    }

    private Session session() {
        return new Session(user(), "access-token", "refresh-token");
    }

    @Test
    void accessors_returnConstructedValues() {
        Session s = session();
        assertThat(s.user()).isEqualTo(user());
        assertThat(s.accessAuthentication()).isEqualTo("access-token");
        assertThat(s.refreshAuthentication()).isEqualTo("refresh-token");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(session()).isEqualTo(session());
    }

    @Test
    void equals_falseForDifferentToken() {
        Session other = new Session(user(), "other-access", "refresh-token");
        assertThat(session()).isNotEqualTo(other);
    }

    @Test
    void hashCode_consistentWithEquals() {
        assertThat(session().hashCode()).isEqualTo(session().hashCode());
    }

    @Test
    void toString_containsAccessAuthentication() {
        assertThat(session().toString()).contains("access-token");
    }
}
