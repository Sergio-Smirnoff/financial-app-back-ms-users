package com.financialapp.users.application;

import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.RefreshSessionUseCase;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshSessionUseCaseImpl implements RefreshSessionUseCase {

    private final UserRepository repository;
    private final AuthenticationProviderGateway authProvider;

    @Override
    public Session execute(RefreshSessionCommand command) {
        UserId userId = authProvider.getUserId(command.refreshAuthentication());
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new Session(
                user,
                authProvider.generateAuthenticationToken(user),
                authProvider.refreshAuthenticationToken(user)
        );
    }
}
