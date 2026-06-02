package com.financialapp.users.domain.model;

import com.financialapp.users.domain.model.valueObject.UserId;
import java.time.LocalDateTime;

public record User(
        UserId id,
        String email,
        String password,
        String firstName,
        String lastName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
