package com.financialapp.users.application;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.command.UpdateUserProfileCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UpdateUserProfileUseCaseTest {

    private UserRepository repository;
    private UpdateUserProfileUseCaseImpl useCase;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        useCase = new UpdateUserProfileUseCaseImpl(repository);
        sampleUser = new User(new UserId(1L), "test@example.com", "hash", "OldFirst", "OldLast", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldUpdateProfileSuccessfully() {
        when(repository.findById(new UserId(1L))).thenReturn(Optional.of(sampleUser));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = useCase.execute(new UpdateUserProfileCommand(new UserId(1L), "NewFirst", "NewLast"));

        assertThat(updated.firstName()).isEqualTo("NewFirst");
        assertThat(updated.lastName()).isEqualTo("NewLast");
        assertThat(updated.email()).isEqualTo("test@example.com"); // Email stays untouched
    }

    @Test
    void shouldRejectBlankNames() {
        when(repository.findById(new UserId(1L))).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> useCase.execute(new UpdateUserProfileCommand(new UserId(1L), "  ", "NewLast")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> useCase.execute(new UpdateUserProfileCommand(new UserId(1L), "NewFirst", "")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
