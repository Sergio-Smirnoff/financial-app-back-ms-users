package com.financialapp.users.infrastructure.messaging.payload;

public record UserRegisteredData(Long userId, String email, String firstName, String lastName) {
}
