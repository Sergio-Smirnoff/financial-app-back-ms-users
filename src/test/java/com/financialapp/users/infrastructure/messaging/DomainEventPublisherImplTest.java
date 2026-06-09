package com.financialapp.users.infrastructure.messaging;

import com.financialapp.commons.messaging.infrastructure.messaging.relay.OutboxEventPublisher;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherImplTest {

    @Mock
    OutboxEventPublisher outboxEventPublisher;

    @InjectMocks
    DomainEventPublisherImpl publisher;

    @Test
    void publish_delegatesToOutboxEventPublisher() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                new UserId(1L), "a@b.com", "John", "Doe");

        publisher.publish(event);

        verify(outboxEventPublisher).publish(event);
    }
}
