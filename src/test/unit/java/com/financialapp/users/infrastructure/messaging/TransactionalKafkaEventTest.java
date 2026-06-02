package com.financialapp.users.infrastructure.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionalKafkaEventTest {

    @Test
    void accessors_returnConstructedValues() {
        TransactionalKafkaEvent event = new TransactionalKafkaEvent("topic", "key", "payload");
        assertThat(event.topic()).isEqualTo("topic");
        assertThat(event.key()).isEqualTo("key");
        assertThat(event.payload()).isEqualTo("payload");
    }

    @Test
    void equals_trueForSameFields() {
        assertThat(new TransactionalKafkaEvent("t", "k", "p"))
                .isEqualTo(new TransactionalKafkaEvent("t", "k", "p"));
    }

    @Test
    void equals_falseForDifferentTopic() {
        assertThat(new TransactionalKafkaEvent("t1", "k", "p"))
                .isNotEqualTo(new TransactionalKafkaEvent("t2", "k", "p"));
    }

    @Test
    void toString_containsTopic() {
        assertThat(new TransactionalKafkaEvent("my-topic", "k", "p").toString()).contains("my-topic");
    }
}
