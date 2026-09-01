package com.financialapp.users.web.dto.response;

import com.financialapp.users.domain.model.User;

public record UserProfileResponse(
        Long userId,
        String email,
        String firstName,
        String lastName
) {
    public static UserProfileResponse fromDomain(User user) {
        return new UserProfileResponse(
                user.id() != null ? user.id().value() : null,
                user.email(),
                user.firstName(),
                user.lastName()
        );
    }
}
