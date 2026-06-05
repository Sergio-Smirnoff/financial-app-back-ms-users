package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(DomainError.USER_NOT_FOUND, message);
    }
}
