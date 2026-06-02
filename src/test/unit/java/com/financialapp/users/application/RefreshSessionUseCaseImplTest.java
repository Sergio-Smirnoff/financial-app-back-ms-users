package com.financialapp.users.application;

import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshSessionUseCaseImplTest {

    @Mock UserRepository repository;
    @Mock AuthenticationProviderGateway authProvider;

    @InjectMocks RefreshSessionUseCaseImpl useCase;

    private static final RefreshSessionCommand COMMAND = new RefreshSessionCommand("refresh-tok");

    private User user() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void execute_returnsNewSession_whenTokenAndUserAreValid() {
        // Given
        UserId userId = new UserId(1L);
        User user = user();
        when(authProvider.getUserId("refresh-tok")).thenReturn(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(authProvider.generateAuthenticationToken(user)).thenReturn("new-access");
        when(authProvider.refreshAuthenticationToken(user)).thenReturn("new-refresh");

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
        when(authProvider.getUserId("refresh-tok")).thenReturn(userId);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(COMMAND))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }
}
