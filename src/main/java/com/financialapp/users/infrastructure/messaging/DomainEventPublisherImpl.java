package com.financialapp.users.infrastructure.messaging;

import com.financialapp.users.domain.event.DomainEvent;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.event.DomainEventPublisher;
import com.financialapp.users.infrastructure.messaging.payload.UserRegisteredPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private static final String TOPIC_USER_REGISTERED = "user.registered";

    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        if (event instanceof UserRegisteredEvent e) {
            log.info("Queuing user.registered event for userId={}", e.userId().value());
            springEventPublisher.publishEvent(new TransactionalKafkaEvent(
                    TOPIC_USER_REGISTERED,
                    e.userId().value().toString(),
                    UserRegisteredPayload.from(e)
            ));
        }
    }
}
