package com.financialapp.users.application;

import com.financialapp.users.domain.event.DomainEventPublisher;
import com.financialapp.users.domain.event.UserRegisteredEvent;
import com.financialapp.users.domain.exception.DuplicateEmailException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
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
    private final UserSessionRepository userSessionRepository;
    private final PasswordHashGateway passwordHashGateway;
    private final AuthenticationProviderGateway authProvider;
    private final DomainEventPublisher eventPublisher;

    @Override
    public Session execute(RegisterUserCommand command) {
        if (repository.findByEmail(command.email()).isPresent()) {
            throw new DuplicateEmailException(command.email());
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                null,
                command.email(),
                passwordHashGateway.hash(command.password()),
                command.firstName(),
                command.lastName(),
                now,
                now
        );

        User saved = repository.save(user);

        eventPublisher.publish(new UserRegisteredEvent(
                saved.id(), saved.email(), saved.firstName(), saved.lastName()
        ));

        DeviceLabel device = DeviceLabel.fromUserAgent(command.userAgent());
        RefreshTokenId refreshTokenId = RefreshTokenId.generate();

        UserSession session = new UserSession(
                null,
                saved.id(),
                refreshTokenId,
                device,
                command.rememberMe(),
                now,
                now,
                false
        );
        UserSession savedSession = userSessionRepository.save(session);

        String accessToken = authProvider.generateAuthenticationToken(saved, savedSession.id());
        String refreshToken = authProvider.refreshAuthenticationToken(saved, refreshTokenId, command.rememberMe());

        return new Session(saved, accessToken, refreshToken);
    }
}
