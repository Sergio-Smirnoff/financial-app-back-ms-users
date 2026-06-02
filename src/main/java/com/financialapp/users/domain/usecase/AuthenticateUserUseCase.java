package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;

public interface AuthenticateUserUseCase {
    Session execute(AuthenticateUserCommand command);
}
