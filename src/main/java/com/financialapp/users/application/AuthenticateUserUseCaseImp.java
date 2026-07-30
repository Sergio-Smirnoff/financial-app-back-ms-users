package com.financialapp.users.application;

import com.financialapp.users.domain.exception.InvalidCredentialsException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.gateway.PasswordHashGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.DeviceLabel;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.AuthenticateUserUseCase;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticateUserUseCaseImp implements AuthenticateUserUseCase {

    private final UserRepository repository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordHashGateway passwordHashGateway;
    private final AuthenticationProviderGateway authProvider;

    @Override
    public Session execute(AuthenticateUserCommand command) {
        User user = repository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashGateway.matches(command.password(), user.password())) {
            throw new InvalidCredentialsException();
        }

        DeviceLabel device = DeviceLabel.fromUserAgent(command.userAgent());
        RefreshTokenId refreshTokenId = RefreshTokenId.generate();
        LocalDateTime now = LocalDateTime.now();

        UserSession session = new UserSession(
                null,
                user.id(),
                refreshTokenId,
                device,
                command.rememberMe(),
                now,
                now,
                false
        );
        UserSession savedSession = userSessionRepository.save(session);

        String accessToken = authProvider.generateAuthenticationToken(user, savedSession.id());
        String refreshToken = authProvider.refreshAuthenticationToken(user, refreshTokenId, command.rememberMe());

        return new Session(user, accessToken, refreshToken);
    }
}
