package com.financialapp.users.domain.event;

import com.financialapp.users.domain.model.valueObject.UserId;

public record UserRegisteredEvent(
        UserId userId,
        String email,
        String firstName,
        String lastName
) implements DomainEvent {
}
