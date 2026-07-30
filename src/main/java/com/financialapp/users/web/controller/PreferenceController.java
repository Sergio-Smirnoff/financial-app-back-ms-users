package com.financialapp.users.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.usecase.GetUserPreferencesUseCase;
import com.financialapp.users.domain.usecase.UpdateUserPreferencesUseCase;
import com.financialapp.users.domain.usecase.command.UpdateUserPreferencesCommand;
import com.financialapp.users.web.dto.request.UpdateUserPreferencesRequest;
import com.financialapp.users.web.dto.response.UserPreferencesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final GetUserPreferencesUseCase getPreferencesUseCase;
    private final UpdateUserPreferencesUseCase updatePreferencesUseCase;

    @GetMapping
    public ApiResponse<UserPreferencesResponse> getPreferences(@RequestHeader("X-User-Id") Long userId) {
        UserPreferences prefs = getPreferencesUseCase.execute(new UserId(userId));
        return ApiResponse.ok("User preferences retrieved", UserPreferencesResponse.fromDomain(prefs));
    }

    @PutMapping
    public ApiResponse<UserPreferencesResponse> updatePreferences(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UpdateUserPreferencesRequest request) {
        UpdateUserPreferencesCommand command = new UpdateUserPreferencesCommand(
                new UserId(userId),
                request.maxIdleMinutes(),
                request.timezone(),
                request.primaryCurrency(),
                request.secondaryCurrency(),
                request.numberFormat(),
                request.decimals(),
                request.colorForAmounts()
        );
        UserPreferences updated = updatePreferencesUseCase.execute(command);
        return ApiResponse.ok("User preferences updated", UserPreferencesResponse.fromDomain(updated));
    }
}
