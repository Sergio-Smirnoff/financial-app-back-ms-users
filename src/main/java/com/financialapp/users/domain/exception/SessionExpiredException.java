package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class SessionExpiredException extends DomainException {
    public SessionExpiredException(String message) {
        super(DomainError.INVALID_TOKEN, message);
    }
    public SessionExpiredException() {
        super(DomainError.INVALID_TOKEN, "Session expired or revoked");
    }
}
