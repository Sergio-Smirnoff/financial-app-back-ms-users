package com.financialapp.users.application;

import com.financialapp.users.domain.event.DomainEventPublisher;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.exception.DuplicateEmailException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.command.RegisterUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock UserRepository repository;
    @Mock PasswordHashGateway passwordHashGateway;
    @Mock AuthenticationProviderGateway authProvider;
    @Mock DomainEventPublisher eventPublisher;

    @InjectMocks RegisterUserUseCaseImpl useCase;

    private static final RegisterUserCommand COMMAND =
            new RegisterUserCommand("a@b.com", "secret", "John", "Doe");

    private User savedUser() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void execute_returnSession_whenEmailIsNew() {
        // Given
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(passwordHashGateway.hash("secret")).thenReturn("hashed");
        User saved = savedUser();
        when(repository.save(any())).thenReturn(saved);
        when(authProvider.generateAuthenticationToken(saved)).thenReturn("access");
        when(authProvider.refreshAuthenticationToken(saved)).thenReturn("refresh");

        // When
        Session session = useCase.execute(COMMAND);

        // Then
        assertThat(session.user()).isEqualTo(saved);
        assertThat(session.accessAuthentication()).isEqualTo("access");
        assertThat(session.refreshAuthentication()).isEqualTo("refresh");
    }

    @Test
    void execute_publishesUserRegisteredEvent_afterSave() {
        // Given
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordHashGateway.hash(any())).thenReturn("hashed");
        User saved = savedUser();
        when(repository.save(any())).thenReturn(saved);
        when(authProvider.generateAuthenticationToken(any())).thenReturn("access");
        when(authProvider.refreshAuthenticationToken(any())).thenReturn("refresh");

        // When
        useCase.execute(COMMAND);

        // Then
        ArgumentCaptor<UserRegisteredEvent> captor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(eventPublisher).publish(captor.capture());
        UserRegisteredEvent event = captor.getValue();
        assertThat(event.userId()).isEqualTo(new UserId(1L));
        assertThat(event.email()).isEqualTo("a@b.com");
        assertThat(event.firstName()).isEqualTo("John");
        assertThat(event.lastName()).isEqualTo("Doe");
    }

    @Test
    void execute_throwsDuplicateEmailException_whenEmailAlreadyExists() {
        // Given
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(savedUser()));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(COMMAND))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("a@b.com");

        verify(repository, never()).save(any());
        verifyNoInteractions(authProvider, eventPublisher);
    }
}
