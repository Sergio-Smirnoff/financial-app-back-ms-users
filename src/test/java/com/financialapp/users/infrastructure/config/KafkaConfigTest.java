package com.financialapp.users.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    private final KafkaConfig kafkaConfig = new KafkaConfig();

    @Test
    void userRegisteredTopic_returnsTopicWithCorrectName() {
        NewTopic topic = kafkaConfig.userRegisteredTopic();
        assertThat(topic.name()).isEqualTo("users.user.registered");
    }

    @Test
    void userRegisteredTopic_returnsNewTopicInstance() {
        assertThat(kafkaConfig.userRegisteredTopic()).isInstanceOf(NewTopic.class);
    }
}
