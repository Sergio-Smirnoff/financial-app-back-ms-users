package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.usecase.command.UpdateUserPreferencesCommand;

public interface UpdateUserPreferencesUseCase {
    UserPreferences execute(UpdateUserPreferencesCommand command);
}
