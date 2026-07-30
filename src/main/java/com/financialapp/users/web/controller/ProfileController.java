package com.financialapp.users.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.commons.web.openapi.ApiErrorCodes;
import com.financialapp.users.domain.exception.DomainError;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.usecase.UpdateUserPasswordUseCase;
import com.financialapp.users.domain.usecase.UpdateUserProfileUseCase;
import com.financialapp.users.domain.usecase.command.UpdateUserPasswordCommand;
import com.financialapp.users.domain.usecase.command.UpdateUserProfileCommand;
import com.financialapp.users.web.dto.request.UpdateUserPasswordRequest;
import com.financialapp.users.web.dto.request.UpdateUserProfileRequest;
import com.financialapp.users.web.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class ProfileController {

    private final UpdateUserProfileUseCase updateProfileUseCase;
    private final UpdateUserPasswordUseCase updatePasswordUseCase;
    private final AuthenticationProviderGateway authProvider;

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                new UserId(userId),
                request.firstName(),
                request.lastName()
        );
        User updated = updateProfileUseCase.execute(command);
        return ApiResponse.ok("Profile updated successfully", UserProfileResponse.fromDomain(updated));
    }

    @PutMapping("/password")
    @ApiErrorCodes(catalog = DomainError.class, value = {"wrong_current_password", "weak_password", "user_not_found"})
    public ApiResponse<Void> updatePassword(
            @RequestHeader("X-User-Id") Long userId,
            @CookieValue(name = "access_token", required = false) String accessToken,
            @Valid @RequestBody UpdateUserPasswordRequest request) {
        Long currentSid = authProvider.getSessionIdFromAccessToken(accessToken);
        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand(
                new UserId(userId),
                request.currentPassword(),
                request.newPassword(),
                currentSid
        );
        updatePasswordUseCase.execute(command);
        return ApiResponse.ok("Password changed successfully", null);
    }
}
