package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.util.List;

public interface ListUserSessionsUseCase {
    List<UserSession> execute(UserId userId);
}
