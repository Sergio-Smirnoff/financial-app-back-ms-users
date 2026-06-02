package com.financialapp.users.domain.model;

public record Session(
        User user,
        String accessAuthentication,
        String refreshAuthentication
) {
}
