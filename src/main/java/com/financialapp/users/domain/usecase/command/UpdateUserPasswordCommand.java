package com.financialapp.users.domain.usecase.command;

import com.financialapp.users.domain.model.valueObject.UserId;

public record UpdateUserPasswordCommand(
        UserId userId,
        String currentPassword,
        String newPassword,
        Long currentSessionId
) {
    public UpdateUserPasswordCommand(UserId userId, String currentPassword, String newPassword) {
        this(userId, currentPassword, newPassword, null);
    }
}
