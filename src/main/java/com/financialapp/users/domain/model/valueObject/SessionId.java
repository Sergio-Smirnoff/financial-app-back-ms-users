package com.financialapp.users.domain.model.valueObject;

import java.util.Objects;

public record SessionId(Long value) {

    public SessionId {
        Objects.requireNonNull(value, "SessionId value cannot be null");
    }
}
