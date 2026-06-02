package com.financialapp.users.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionalKafkaListenerTest {

    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks TransactionalKafkaListener listener;

    @Test
    void handle_sendsEventToKafka() {
        TransactionalKafkaEvent event = new TransactionalKafkaEvent("user.registered", "1", "payload");

        listener.handle(event);

        verify(kafkaTemplate).send("user.registered", "1", "payload");
    }
}
