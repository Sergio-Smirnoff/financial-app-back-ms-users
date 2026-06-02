package com.financialapp.users.infrastructure.messaging.payload;

import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisteredPayloadTest {

    private UserRegisteredEvent event() {
        return new UserRegisteredEvent(new UserId(3L), "a@b.com", "John", "Doe");
    }

    @Test
    void from_mapsEventType() {
        assertThat(UserRegisteredPayload.from(event()).eventType()).isEqualTo("USER_REGISTERED");
    }

    @Test
    void from_mapsUserId() {
        assertThat(UserRegisteredPayload.from(event()).userId()).isEqualTo(3L);
    }

    @Test
    void from_setsTimestamp() {
        assertThat(UserRegisteredPayload.from(event()).timestamp()).isNotNull();
    }

    @Test
    void from_mapsPayloadFields() {
        UserRegisteredPayload.Payload payload = UserRegisteredPayload.from(event()).payload();
        assertThat(payload.email()).isEqualTo("a@b.com");
        assertThat(payload.firstName()).isEqualTo("John");
        assertThat(payload.lastName()).isEqualTo("Doe");
    }

    @Test
    void accessors_roundTrip() {
        UserRegisteredPayload p = new UserRegisteredPayload(
                "USER_REGISTERED", 5L, null,
                new UserRegisteredPayload.Payload("x@y.com", "Jane", "Smith"));
        assertThat(p.eventType()).isEqualTo("USER_REGISTERED");
        assertThat(p.userId()).isEqualTo(5L);
        assertThat(p.payload().email()).isEqualTo("x@y.com");
    }
}
