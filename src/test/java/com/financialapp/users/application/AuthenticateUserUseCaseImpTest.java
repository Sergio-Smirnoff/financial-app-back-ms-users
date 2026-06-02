package com.financialapp.users.application;

import com.financialapp.users.domain.exception.InvalidCredentialsException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImpTest {

    @Mock UserRepository repository;
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
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordHashGateway.matches("secret", "hashed")).thenReturn(true);
        when(authProvider.generateAuthenticationToken(user)).thenReturn("access");
        when(authProvider.refreshAuthenticationToken(user)).thenReturn("refresh");

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
