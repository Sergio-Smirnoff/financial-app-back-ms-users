package com.financialapp.users.infrastructure.messaging;

import com.financialapp.commons.messaging.infrastructure.messaging.relay.OutboxEventPublisher;
import com.financialapp.users.domain.event.DomainEvent;
import com.financialapp.users.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    @Transactional
    public void publish(DomainEvent event) {
        outboxEventPublisher.publish(event);
    }
}
