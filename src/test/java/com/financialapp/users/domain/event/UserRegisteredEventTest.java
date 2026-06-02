package com.financialapp.users.domain.event;

import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisteredEventTest {

    private UserRegisteredEvent event() {
        return new UserRegisteredEvent(new UserId(1L), "a@b.com", "John", "Doe");
    }

    @Test
    void accessors_returnConstructedValues() {
        UserRegisteredEvent e = event();
        assertThat(e.userId()).isEqualTo(new UserId(1L));
        assertThat(e.email()).isEqualTo("a@b.com");
        assertThat(e.firstName()).isEqualTo("John");
        assertThat(e.lastName()).isEqualTo("Doe");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(event()).isEqualTo(event());
    }

    @Test
    void equals_falseForDifferentEmail() {
        UserRegisteredEvent other = new UserRegisteredEvent(new UserId(1L), "x@y.com", "John", "Doe");
        assertThat(event()).isNotEqualTo(other);
    }

    @Test
    void hashCode_consistentWithEquals() {
        assertThat(event().hashCode()).isEqualTo(event().hashCode());
    }

    @Test
    void toString_containsEmail() {
        assertThat(event().toString()).contains("a@b.com");
    }

    @Test
    void implementsDomainEvent() {
        assertThat(event()).isInstanceOf(DomainEvent.class);
    }
}
