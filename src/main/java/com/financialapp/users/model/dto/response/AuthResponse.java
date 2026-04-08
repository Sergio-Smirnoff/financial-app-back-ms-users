package com.financialapp.users.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private final Long userId;
    private final String email;
    private final String firstName;
    private final String lastName;
}
