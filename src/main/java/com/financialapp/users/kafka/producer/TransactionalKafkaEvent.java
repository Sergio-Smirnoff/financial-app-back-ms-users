package com.financialapp.users.kafka.producer;

public record TransactionalKafkaEvent(String topic, String key, Object payload) {}
