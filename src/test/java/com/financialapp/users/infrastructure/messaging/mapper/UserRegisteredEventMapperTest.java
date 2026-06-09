package com.financialapp.users.infrastructure.messaging.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRegisteredEventMapperTest {

    private final UserRegisteredEventMapper mapper =
            new UserRegisteredEventMapper(new ObjectMapper());

    @Test
    void mapsToUserRegisteredOutboxRecord() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                new UserId(42L), "a@b.c", "Ada", "Lovelace");

        List<OutboxRecord> records = mapper.toOutboxRecords(event);

        assertThat(records).hasSize(1);
        OutboxRecord r = records.get(0);
        assertThat(r.topic()).isEqualTo("users.user.registered");
        assertThat(r.type().value()).isEqualTo("users.user.registered");
        assertThat(r.key()).isEqualTo("42");
        assertThat(r.source()).isEqualTo("ms-users");
        assertThat(r.dataSchema()).isEqualTo("https://schemas.financial-app/users/user-registered/v1");
    }

    @Test
    void dataJsonContainsAllRequiredFields() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                new UserId(42L), "a@b.c", "Ada", "Lovelace");

        OutboxRecord r = mapper.toOutboxRecords(event).get(0);

        assertThat(r.dataJson()).contains("\"email\"").contains("a@b.c");
        assertThat(r.dataJson()).contains("\"firstName\"").contains("Ada");
        assertThat(r.dataJson()).contains("\"lastName\"").contains("Lovelace");
        assertThat(r.dataJson()).contains("\"userId\"").contains("42");
    }

    @Test
    void supportsOnlyUserRegisteredEvent() {
        assertThat(mapper.supports(new UserRegisteredEvent(
                new UserId(1L), "x@y.z", "A", "B"))).isTrue();
        assertThat(mapper.supports(new Object())).isFalse();
    }

    @Test
    void serializeThrowsIllegalStateExceptionOnJsonError() throws JsonProcessingException {
        ObjectMapper brokenMapper = mock(ObjectMapper.class);
        when(brokenMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("boom") {});
        UserRegisteredEventMapper failingMapper = new UserRegisteredEventMapper(brokenMapper);
        UserRegisteredEvent event = new UserRegisteredEvent(
                new UserId(1L), "x@y.z", "A", "B");

        assertThatThrownBy(() -> failingMapper.toOutboxRecords(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to serialize event data");
    }
}
