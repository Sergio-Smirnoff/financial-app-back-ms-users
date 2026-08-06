package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;

public interface GetUserProfileUseCase {
    User execute(UserId userId);
}
