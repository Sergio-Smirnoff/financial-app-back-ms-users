package com.financialapp.users.infrastructure.messaging.payload;

import com.financialapp.users.domain.event.UserRegisteredEvent;

import java.time.LocalDateTime;

public record UserRegisteredPayload(
        String eventType,
        Long userId,
        LocalDateTime timestamp,
        Payload payload
) {
    public record Payload(String email, String firstName, String lastName) {}

    public static UserRegisteredPayload from(UserRegisteredEvent event) {
        return new UserRegisteredPayload(
                "USER_REGISTERED",
                event.userId().value(),
                LocalDateTime.now(),
                new Payload(event.email(), event.firstName(), event.lastName())
        );
    }
}
