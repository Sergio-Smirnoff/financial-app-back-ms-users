package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class WrongCurrentPasswordException extends DomainException {
    public WrongCurrentPasswordException() {
        super(DomainError.WRONG_CURRENT_PASSWORD, "Current password does not match");
    }
}
