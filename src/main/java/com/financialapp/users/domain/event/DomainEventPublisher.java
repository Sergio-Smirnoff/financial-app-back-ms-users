package com.financialapp.users.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
