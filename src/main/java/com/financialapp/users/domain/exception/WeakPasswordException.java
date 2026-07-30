package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class WeakPasswordException extends DomainException {
    public WeakPasswordException() {
        super(DomainError.WEAK_PASSWORD, "New password must be at least 8 characters long");
    }
}
