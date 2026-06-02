package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;

public interface RefreshSessionUseCase {
    Session execute(RefreshSessionCommand command);
}
