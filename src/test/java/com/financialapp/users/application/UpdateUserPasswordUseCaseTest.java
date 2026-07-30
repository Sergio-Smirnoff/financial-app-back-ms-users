package com.financialapp.users.application;

import com.financialapp.users.domain.exception.WeakPasswordException;
import com.financialapp.users.domain.exception.WrongCurrentPasswordException;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.command.UpdateUserPasswordCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateUserPasswordUseCaseTest {

    private UserRepository userRepository;
    private UserSessionRepository userSessionRepository;
    private PasswordHashGateway passwordHashGateway;
    private UpdateUserPasswordUseCaseImpl useCase;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userSessionRepository = Mockito.mock(UserSessionRepository.class);
        passwordHashGateway = Mockito.mock(PasswordHashGateway.class);
        useCase = new UpdateUserPasswordUseCaseImpl(userRepository, userSessionRepository, passwordHashGateway);

        sampleUser = new User(new UserId(1L), "test@example.com", "old_hashed_pass", "John", "Doe", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldRejectWrongCurrentPassword() {
        when(userRepository.findById(new UserId(1L))).thenReturn(Optional.of(sampleUser));
        when(passwordHashGateway.matches("wrong_pass", "old_hashed_pass")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new UpdateUserPasswordCommand(new UserId(1L), "wrong_pass", "new_valid_pass_123")))
                .isInstanceOf(WrongCurrentPasswordException.class);
    }

    @Test
    void shouldRejectWeakNewPassword() {
        when(userRepository.findById(new UserId(1L))).thenReturn(Optional.of(sampleUser));
        when(passwordHashGateway.matches("old_pass", "old_hashed_pass")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new UpdateUserPasswordCommand(new UserId(1L), "old_pass", "short")))
                .isInstanceOf(WeakPasswordException.class);
    }

    @Test
    void shouldRehashPasswordAndRevokeOtherSessionsOnSuccess() {
        when(userRepository.findById(new UserId(1L))).thenReturn(Optional.of(sampleUser));
        when(passwordHashGateway.matches("old_pass", "old_hashed_pass")).thenReturn(true);
        when(passwordHashGateway.hash("new_secret_pass_123")).thenReturn("new_hashed_pass");

        LocalDateTime now = LocalDateTime.now();
        UserSession currentSession = new UserSession(new SessionId(10L), new UserId(1L), RefreshTokenId.generate(), new DeviceLabel("Chrome"), false, now, now, false);
        UserSession otherSession1 = new UserSession(new SessionId(20L), new UserId(1L), RefreshTokenId.generate(), new DeviceLabel("Firefox"), false, now, now, false);
        UserSession otherSession2 = new UserSession(new SessionId(30L), new UserId(1L), RefreshTokenId.generate(), new DeviceLabel("Safari"), false, now, now, false);

        when(userSessionRepository.findByUser(new UserId(1L))).thenReturn(List.of(currentSession, otherSession1, otherSession2));

        useCase.execute(new UpdateUserPasswordCommand(new UserId(1L), "old_pass", "new_secret_pass_123", 10L));

        // Verify password hash updated
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().password()).isEqualTo("new_hashed_pass");

        // Verify other sessions revoked while current session (id 10L) survives
        ArgumentCaptor<UserSession> sessionCaptor = ArgumentCaptor.forClass(UserSession.class);
        verify(userSessionRepository, times(2)).save(sessionCaptor.capture());

        List<UserSession> revokedSessions = sessionCaptor.getAllValues();
        assertThat(revokedSessions).extracting(s -> s.id().value()).containsExactlyInAnyOrder(20L, 30L);
        assertThat(revokedSessions).allMatch(UserSession::revoked);
    }
}
