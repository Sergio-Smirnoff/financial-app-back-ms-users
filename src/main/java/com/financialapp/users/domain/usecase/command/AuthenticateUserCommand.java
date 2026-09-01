package com.financialapp.users.domain.usecase.command;

public record AuthenticateUserCommand(
        String email,
        String password,
        boolean rememberMe,
        String userAgent
) {
    public AuthenticateUserCommand(String email, String password) {
        this(email, password, false, null);
    }
}
