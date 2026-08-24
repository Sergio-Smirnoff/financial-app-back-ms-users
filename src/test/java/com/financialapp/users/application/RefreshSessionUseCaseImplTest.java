package com.financialapp.users.application;

import com.financialapp.users.domain.exception.SessionExpiredException;
import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.InactivityPolicy;
import com.financialapp.users.domain.model.valueObject.RefreshTokenClaims;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserPreferencesRepository;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshSessionUseCaseImplTest {

    @Mock UserRepository repository;
    @Mock UserSessionRepository userSessionRepository;
    @Mock AuthenticationProviderGateway authProvider;
    @Mock UserPreferencesRepository preferencesRepository;

    @InjectMocks RefreshSessionUseCaseImpl useCase;

    private static final RefreshSessionCommand COMMAND = new RefreshSessionCommand("refresh-tok");

    private User user() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
    }

    private UserPreferences preferencesWithIdleMinutes(UserId userId, int minutes) {
        return new UserPreferences(userId, InactivityPolicy.fromMinutes(minutes),
                "America/Argentina/Buenos_Aires", "ARS", null, "es-AR", 2, true);
    }

    @Test
    void execute_returnsNewSession_whenTokenAndUserAreValid() {
        // Given
        UserId userId = new UserId(1L);
        User user = user();
        RefreshTokenId jti = RefreshTokenId.generate();
        RefreshTokenClaims claims = new RefreshTokenClaims(userId, jti);
        UserSession userSession = new UserSession(new SessionId(100L), userId, jti, DeviceLabel.fromUserAgent(null), false, LocalDateTime.now(), LocalDateTime.now(), false);

        when(authProvider.getRefreshTokenClaims("refresh-tok")).thenReturn(claims);
        when(userSessionRepository.findByRefreshTokenId(jti)).thenReturn(Optional.of(userSession));
        when(preferencesRepository.findByUser(userId)).thenReturn(preferencesWithIdleMinutes(userId, 30));
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(authProvider.generateAuthenticationToken(any(User.class), any())).thenReturn("new-access");
        when(authProvider.refreshAuthenticationToken(any(User.class), any(), anyBoolean())).thenReturn("new-refresh");

        // When
        Session session = useCase.execute(COMMAND);

        // Then
        assertThat(session.user()).isEqualTo(user);
        assertThat(session.accessAuthentication()).isEqualTo("new-access");
        assertThat(session.refreshAuthentication()).isEqualTo("new-refresh");
    }

    @Test
    void execute_throwsUserNotFoundException_whenUserDoesNotExist() {
        // Given
        UserId userId = new UserId(99L);
        RefreshTokenId jti = RefreshTokenId.generate();
        RefreshTokenClaims claims = new RefreshTokenClaims(userId, jti);
        UserSession userSession = new UserSession(new SessionId(100L), userId, jti, DeviceLabel.fromUserAgent(null), false, LocalDateTime.now(), LocalDateTime.now(), false);

        when(authProvider.getRefreshTokenClaims("refresh-tok")).thenReturn(claims);
        when(userSessionRepository.findByRefreshTokenId(jti)).thenReturn(Optional.of(userSession));
        when(preferencesRepository.findByUser(userId)).thenReturn(preferencesWithIdleMinutes(userId, 30));
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(COMMAND))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void execute_revokesSessionAndThrows_whenInactivityPolicyIsExceeded() {
        // Given
        UserId userId = new UserId(1L);
        RefreshTokenId jti = RefreshTokenId.generate();
        RefreshTokenClaims claims = new RefreshTokenClaims(userId, jti);
        LocalDateTime lastSeen = LocalDateTime.now().minusMinutes(45);
        UserSession userSession = new UserSession(new SessionId(100L), userId, jti, DeviceLabel.fromUserAgent(null), false, lastSeen, lastSeen, false);

        when(authProvider.getRefreshTokenClaims("refresh-tok")).thenReturn(claims);
        when(userSessionRepository.findByRefreshTokenId(jti)).thenReturn(Optional.of(userSession));
        when(preferencesRepository.findByUser(userId)).thenReturn(preferencesWithIdleMinutes(userId, 30));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(COMMAND))
                .isInstanceOf(SessionExpiredException.class)
                .hasMessage("Session expired due to inactivity");

        verify(userSessionRepository).save(userSession.revoke());
    }

    @Test
    void execute_returnsNewSession_whenInactivityPolicyIsDisabled() {
        // Given
        UserId userId = new UserId(1L);
        User user = user();
        RefreshTokenId jti = RefreshTokenId.generate();
        RefreshTokenClaims claims = new RefreshTokenClaims(userId, jti);
        LocalDateTime lastSeen = LocalDateTime.now().minusDays(30);
        UserSession userSession = new UserSession(new SessionId(100L), userId, jti, DeviceLabel.fromUserAgent(null), false, lastSeen, lastSeen, false);

        when(authProvider.getRefreshTokenClaims("refresh-tok")).thenReturn(claims);
        when(userSessionRepository.findByRefreshTokenId(jti)).thenReturn(Optional.of(userSession));
        when(preferencesRepository.findByUser(userId)).thenReturn(preferencesWithIdleMinutes(userId, -1));
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(authProvider.generateAuthenticationToken(any(User.class), any())).thenReturn("new-access");
        when(authProvider.refreshAuthenticationToken(any(User.class), any(), anyBoolean())).thenReturn("new-refresh");

        // When
        Session session = useCase.execute(COMMAND);

        // Then
        assertThat(session.accessAuthentication()).isEqualTo("new-access");
    }
}
