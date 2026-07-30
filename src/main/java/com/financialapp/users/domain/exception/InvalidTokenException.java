package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException(String message) {
        super(DomainError.INVALID_TOKEN, message);
    }
    public InvalidTokenException() {
        super(DomainError.INVALID_TOKEN, "Invalid token");
    }
}
