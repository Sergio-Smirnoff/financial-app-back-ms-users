package com.financialapp.users.domain.usecase.command;

public record RefreshSessionCommand(
        String refreshAuthentication,
        String userAgent
) {
    public RefreshSessionCommand(String refreshAuthentication) {
        this(refreshAuthentication, null);
    }
}
