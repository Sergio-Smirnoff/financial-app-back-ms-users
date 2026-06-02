package com.financialapp.users.domain.usecase.command;

public record RegisterUserCommand(String email, String password, String firstName, String lastName) {}
