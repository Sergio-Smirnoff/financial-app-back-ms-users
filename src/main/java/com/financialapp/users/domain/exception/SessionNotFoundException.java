package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class SessionNotFoundException extends DomainException {
    public SessionNotFoundException() {
        super(DomainError.SESSION_NOT_FOUND, "Session not found or owned by another user");
    }
}
