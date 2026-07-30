package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.UserId;

public interface GetUserPreferencesUseCase {
    UserPreferences execute(UserId userId);
}
