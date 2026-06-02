package com.financialapp.users.infrastructure.messaging;

public record TransactionalKafkaEvent(String topic, String key, Object payload) {}
