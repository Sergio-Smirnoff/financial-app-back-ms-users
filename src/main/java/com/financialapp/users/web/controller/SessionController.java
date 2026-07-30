package com.financialapp.users.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.usecase.ListUserSessionsUseCase;
import com.financialapp.users.domain.usecase.RevokeUserSessionUseCase;
import com.financialapp.users.web.dto.response.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final ListUserSessionsUseCase listUserSessionsUseCase;
    private final RevokeUserSessionUseCase revokeUserSessionUseCase;
    private final AuthenticationProviderGateway authProvider;

    @GetMapping
    public ApiResponse<List<SessionResponse>> listSessions(
            @RequestHeader("X-User-Id") Long userId,
            @CookieValue(name = "access_token", required = false) String accessToken) {
        Long currentSid = authProvider.getSessionIdFromAccessToken(accessToken);
        List<UserSession> sessions = listUserSessionsUseCase.execute(new UserId(userId));

        List<SessionResponse> responseList = sessions.stream()
                .map(s -> {
                    boolean isCurrent = currentSid != null && s.id() != null && currentSid.equals(s.id().value());
                    return SessionResponse.fromDomain(s, isCurrent);
                })
                .toList();

        return ApiResponse.ok("User sessions retrieved", responseList);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> revokeSession(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        revokeUserSessionUseCase.execute(new SessionId(id), new UserId(userId));
        return ApiResponse.ok("Session revoked successfully", null);
    }
}
