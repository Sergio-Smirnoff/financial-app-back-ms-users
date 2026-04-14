package com.financialapp.users.kafka.producer;

import com.financialapp.users.kafka.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private static final String TOPIC_USER_REGISTERED = "user.registered";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("Publishing user.registered event for userId={}", event.getUserId());
        kafkaTemplate.send(TOPIC_USER_REGISTERED, String.valueOf(event.getUserId()), event);
    }
}
