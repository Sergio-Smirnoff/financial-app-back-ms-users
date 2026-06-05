package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super(DomainError.INVALID_CREDENTIALS, "Invalid email or password");
    }
}
