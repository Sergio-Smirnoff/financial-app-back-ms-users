package com.financialapp.users.infrastructure.messaging;

import com.financialapp.users.domain.event.DomainEvent;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherImplTest {

    @Mock ApplicationEventPublisher springEventPublisher;
    @InjectMocks DomainEventPublisherImpl publisher;

    @Test
    void publish_sendsTransactionalKafkaEvent_forUserRegisteredEvent() {
        UserRegisteredEvent domainEvent = new UserRegisteredEvent(
                new UserId(1L), "a@b.com", "John", "Doe");

        publisher.publish(domainEvent);

        ArgumentCaptor<TransactionalKafkaEvent> captor =
                ArgumentCaptor.forClass(TransactionalKafkaEvent.class);
        verify(springEventPublisher).publishEvent(captor.capture());
        TransactionalKafkaEvent sent = captor.getValue();
        assertThat(sent.topic()).isEqualTo("user.registered");
        assertThat(sent.key()).isEqualTo("1");
    }

    @Test
    void publish_doesNothing_forUnknownEventType() {
        DomainEvent unknown = new DomainEvent() {};

        publisher.publish(unknown);

        verifyNoInteractions(springEventPublisher);
    }
}
