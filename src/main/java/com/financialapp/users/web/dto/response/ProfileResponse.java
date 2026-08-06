package com.financialapp.users.web.dto.response;

import com.financialapp.users.domain.model.User;

import java.time.Instant;
import java.time.ZoneOffset;

public record ProfileResponse(
        String name,
        String email,
        Instant createdAt
) {
    public static ProfileResponse fromDomain(User user) {
        String firstName = user.firstName() != null ? user.firstName() : "";
        String lastName = user.lastName() != null ? user.lastName() : "";
        String name = (firstName + " " + lastName).trim();
        Instant createdAt = user.createdAt() != null ? user.createdAt().toInstant(ZoneOffset.UTC) : null;
        return new ProfileResponse(name, user.email(), createdAt);
    }
}
