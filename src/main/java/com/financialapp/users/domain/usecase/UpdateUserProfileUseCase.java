package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.usecase.command.UpdateUserProfileCommand;

public interface UpdateUserProfileUseCase {
    User execute(UpdateUserProfileCommand command);
}
