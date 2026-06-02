package com.financialapp.users.application;

import com.financialapp.users.domain.exception.InvalidCredentialsException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.AuthenticateUserUseCase;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUserUseCaseImp implements AuthenticateUserUseCase {

    private final UserRepository repository;
    private final PasswordHashGateway passwordHashGateway;
    private final AuthenticationProviderGateway authProvider;

    @Override
    public Session execute(AuthenticateUserCommand command) {
        User user = repository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashGateway.matches(command.password(), user.password())) {
            throw new InvalidCredentialsException();
        }

        return new Session(
                user,
                authProvider.generateAuthenticationToken(user),
                authProvider.refreshAuthenticationToken(user)
        );
    }
}
