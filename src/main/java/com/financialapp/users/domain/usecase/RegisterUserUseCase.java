package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.usecase.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    Session execute(RegisterUserCommand command);
}
