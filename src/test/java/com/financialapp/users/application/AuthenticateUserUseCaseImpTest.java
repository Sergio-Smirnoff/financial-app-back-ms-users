package com.financialapp.users.application;

import com.financialapp.users.domain.exception.InvalidCredentialsException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImpTest {

    @Mock UserRepository repository;
    @Mock UserSessionRepository userSessionRepository;
    @Mock PasswordHashGateway passwordHashGateway;
    @Mock AuthenticationProviderGateway authProvider;

    @InjectMocks AuthenticateUserUseCaseImp useCase;

    private static final AuthenticateUserCommand COMMAND =
            new AuthenticateUserCommand("a@b.com", "secret");

    private User user() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void execute_returnsSession_whenCredentialsAreValid() {
        // Given
        User user = user();
        UserSession mockSession = new UserSession(new SessionId(100L), user.id(), RefreshTokenId.generate(), DeviceLabel.fromUserAgent(null), false, LocalDateTime.now(), LocalDateTime.now(), false);
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordHashGateway.matches("secret", "hashed")).thenReturn(true);
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(mockSession);
        when(authProvider.generateAuthenticationToken(any(User.class), any(SessionId.class))).thenReturn("access");
        when(authProvider.refreshAuthenticationToken(any(User.class), any(RefreshTokenId.class), anyBoolean())).thenReturn("refresh");

        // When
        Session session = useCase.execute(COMMAND);

        // Then
        assertThat(session.user()).isEqualTo(user);
        assertThat(session.accessAuthentication()).isEqualTo("access");
        assertThat(session.refreshAuthentication()).isEqualTo("refresh");
    }

    @Test
    void execute_throwsInvalidCredentials_whenEmailNotFound() {
        // Given
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(COMMAND))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(authProvider);
    }

    @Test
    void execute_throwsInvalidCredentials_whenPasswordDoesNotMatch() {
        // Given
        User user = user();
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordHashGateway.matches("secret", "hashed")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(COMMAND))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(authProvider);
    }
}
