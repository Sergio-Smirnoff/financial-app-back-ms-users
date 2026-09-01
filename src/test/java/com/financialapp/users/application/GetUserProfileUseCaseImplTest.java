package com.financialapp.users.application;

import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserProfileUseCaseImplTest {

    private UserRepository repository;
    private GetUserProfileUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        useCase = new GetUserProfileUseCaseImpl(repository);
    }

    @Test
    void execute_returnsUserWhenFound() {
        UserId userId = new UserId(5L);
        User user = new User(userId, "ana@example.com", "pass", "Ana", "Silva", LocalDateTime.now(), LocalDateTime.now());
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User result = useCase.execute(userId);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void execute_throwsUserNotFoundExceptionWhenNotFound() {
        UserId userId = new UserId(99L);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
