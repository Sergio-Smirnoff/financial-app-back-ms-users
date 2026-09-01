package com.financialapp.users.domain.usecase.command;

public record RegisterUserCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        boolean rememberMe,
        String userAgent
) {
    public RegisterUserCommand(String email, String password, String firstName, String lastName) {
        this(email, password, firstName, lastName, false, null);
    }
}
