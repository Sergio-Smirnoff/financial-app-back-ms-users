package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class DuplicateEmailException extends DomainException {
    public DuplicateEmailException(String email) {
        super(DomainError.EMAIL_ALREADY_REGISTERED, "Email already registered: " + email);
    }
}
