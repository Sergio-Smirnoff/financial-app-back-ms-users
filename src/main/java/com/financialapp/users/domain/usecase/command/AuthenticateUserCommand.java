package com.financialapp.users.domain.usecase.command;

public record AuthenticateUserCommand(String email, String password) {
}
