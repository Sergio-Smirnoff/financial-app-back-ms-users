package com.financialapp.users.application;

import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.exception.DuplicateEmailException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.event.DomainEventPublisher;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.RegisterUserUseCase;
import com.financialapp.users.domain.usecase.command.RegisterUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository repository;
    private final PasswordHashGateway passwordHashGateway;
    private final AuthenticationProviderGateway authProvider;
    private final DomainEventPublisher eventPublisher;

    @Override
    public Session execute(RegisterUserCommand command) {
        if (repository.findByEmail(command.email()).isPresent()) {
            throw new DuplicateEmailException(command.email());
        }

        User user = new User(
                null,
                command.email(),
                passwordHashGateway.hash(command.password()),
                command.firstName(),
                command.lastName(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User saved = repository.save(user);

        eventPublisher.publish(new UserRegisteredEvent(
                saved.id(), saved.email(), saved.firstName(), saved.lastName()
        ));

        return new Session(
                saved,
                authProvider.generateAuthenticationToken(saved),
                authProvider.refreshAuthenticationToken(saved)
        );
    }
}
