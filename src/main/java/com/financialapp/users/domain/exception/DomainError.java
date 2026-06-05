package com.financialapp.users.domain.exception;

import com.financialapp.commons.core.error.ErrorCategory;
import com.financialapp.commons.core.error.ErrorCode;

public enum DomainError implements ErrorCode {

    INVALID_CREDENTIALS(ErrorCategory.UNAUTHORIZED, "invalid_credentials"),
    EMAIL_ALREADY_REGISTERED(ErrorCategory.CONFLICT, "email_already_registered"),
    USER_NOT_FOUND(ErrorCategory.NOT_FOUND, "user_not_found"),
    INVALID_TOKEN(ErrorCategory.UNAUTHORIZED, "invalid_token"),
    AUTHENTICATION_REQUIRED(ErrorCategory.UNAUTHORIZED, "authentication_required"),
    INTERNAL_ERROR(ErrorCategory.INTERNAL_SERVER_ERROR, "internal_error");

    private final ErrorCategory category;
    private final String code;

    DomainError(ErrorCategory category, String code) {
        this.category = category;
        this.code = code;
    }

    @Override
    public ErrorCategory category() { return category; }

    @Override
    public String code() { return code; }
}
