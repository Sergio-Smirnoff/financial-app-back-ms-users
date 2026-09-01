package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.usecase.command.UpdateUserPasswordCommand;

public interface UpdateUserPasswordUseCase {
    void execute(UpdateUserPasswordCommand command);
}
