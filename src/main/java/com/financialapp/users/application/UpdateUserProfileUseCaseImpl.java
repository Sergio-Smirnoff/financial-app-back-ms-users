package com.financialapp.users.application;

import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.UpdateUserProfileUseCase;
import com.financialapp.users.domain.usecase.command.UpdateUserProfileCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepository repository;

    @Override
    public User execute(UpdateUserProfileCommand command) {
        User user = repository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String firstName = command.firstName() != null ? command.firstName().trim() : "";
        String lastName = command.lastName() != null ? command.lastName().trim() : "";

        if (firstName.isBlank() || firstName.length() > 100) {
            throw new IllegalArgumentException("firstName must be non-blank and max 100 characters");
        }
        if (lastName.isBlank() || lastName.length() > 100) {
            throw new IllegalArgumentException("lastName must be non-blank and max 100 characters");
        }

        User updatedUser = new User(
                user.id(),
                user.email(),
                user.password(),
                firstName,
                lastName,
                user.createdAt(),
                LocalDateTime.now()
        );

        return repository.save(updatedUser);
    }
}
