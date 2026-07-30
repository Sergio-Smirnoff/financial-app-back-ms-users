package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;

public interface RevokeUserSessionUseCase {
    void execute(SessionId sessionId, UserId userId);
}
