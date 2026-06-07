package com.financialapp.users.infrastructure.messaging.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.commons.messaging.domain.gateway.TypedDomainEventMapper;
import com.financialapp.commons.messaging.domain.model.EventType;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.infrastructure.messaging.payload.UserRegisteredData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRegisteredEventMapper extends TypedDomainEventMapper<UserRegisteredEvent> {

    private static final String TOPIC = "users.user.registered";
    private static final String SCHEMA = "https://schemas.financial-app/users/user-registered/v1";

    private final ObjectMapper objectMapper;

    public UserRegisteredEventMapper(ObjectMapper objectMapper) {
        super(UserRegisteredEvent.class);
        this.objectMapper = objectMapper;
    }

    @Override
    protected List<OutboxRecord> mapTyped(UserRegisteredEvent event) {
        UserRegisteredData data = new UserRegisteredData(
                event.userId().value(),
                event.email(),
                event.firstName(),
                event.lastName());
        return List.of(OutboxRecord.create(
                TOPIC,
                String.valueOf(event.userId().value()),
                new EventType(TOPIC),
                "ms-users",
                SCHEMA,
                serialize(data)));
    }

    private String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize event data", ex);
        }
    }
}
