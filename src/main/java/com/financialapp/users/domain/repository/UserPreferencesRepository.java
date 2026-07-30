package com.financialapp.users.domain.repository;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.UserId;

public interface UserPreferencesRepository {
    UserPreferences findByUser(UserId userId);
    UserPreferences save(UserPreferences preferences);
}
