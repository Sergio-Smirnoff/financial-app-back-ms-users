package com.financialapp.users.kafka.producer;

import com.financialapp.users.kafka.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private static final String TOPIC_USER_REGISTERED = "user.registered";

    private final ApplicationEventPublisher eventPublisher;

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("Queuing transactional user.registered event for userId={}", event.getUserId());
        eventPublisher.publishEvent(new TransactionalKafkaEvent(TOPIC_USER_REGISTERED, String.valueOf(event.getUserId()), event));
    }
}
