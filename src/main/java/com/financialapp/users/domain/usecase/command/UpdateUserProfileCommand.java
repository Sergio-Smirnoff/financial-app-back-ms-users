package com.financialapp.users.domain.usecase.command;

import com.financialapp.users.domain.model.valueObject.UserId;

public record UpdateUserProfileCommand(
        UserId userId,
        String firstName,
        String lastName
) {
}
