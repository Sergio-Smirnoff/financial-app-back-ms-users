package com.financialapp.users.application;

import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.exception.WeakPasswordException;
import com.financialapp.users.domain.exception.WrongCurrentPasswordException;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.UpdateUserPasswordUseCase;
import com.financialapp.users.domain.usecase.command.UpdateUserPasswordCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserPasswordUseCaseImpl implements UpdateUserPasswordUseCase {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordHashGateway passwordHashGateway;

    @Override
    public void execute(UpdateUserPasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordHashGateway.matches(command.currentPassword(), user.password())) {
            throw new WrongCurrentPasswordException();
        }

        if (command.newPassword() == null || command.newPassword().length() < 8) {
            throw new WeakPasswordException();
        }

        String newHash = passwordHashGateway.hash(command.newPassword());
        User updatedUser = new User(
                user.id(),
                user.email(),
                newHash,
                user.firstName(),
                user.lastName(),
                user.createdAt(),
                LocalDateTime.now()
        );
        userRepository.save(updatedUser);

        // Revoke every OTHER session of the user; current session survives
        List<UserSession> sessions = userSessionRepository.findByUser(command.userId());
        for (UserSession session : sessions) {
            if (command.currentSessionId() != null
                    && session.id() != null
                    && session.id().value().equals(command.currentSessionId())) {
                continue; // keep current session alive
            }
            if (!session.revoked()) {
                userSessionRepository.save(session.revoke());
            }
        }
    }
}
