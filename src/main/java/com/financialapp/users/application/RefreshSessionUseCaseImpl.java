package com.financialapp.users.application;

import com.financialapp.users.domain.exception.SessionExpiredException;
import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.RefreshTokenClaims;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.repository.UserPreferencesRepository;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.RefreshSessionUseCase;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshSessionUseCaseImpl implements RefreshSessionUseCase {

    private final UserRepository repository;
    private final UserSessionRepository userSessionRepository;
    private final AuthenticationProviderGateway authProvider;

    @Autowired(required = false)
    private UserPreferencesRepository preferencesRepository;

    @Override
    public Session execute(RefreshSessionCommand command) {
        RefreshTokenClaims claims = authProvider.getRefreshTokenClaims(command.refreshAuthentication());

        UserSession session = userSessionRepository.findByRefreshTokenId(claims.jti())
                .orElseThrow(() -> new SessionExpiredException("Session not found for token"));

        if (session.revoked()) {
            throw new SessionExpiredException("Session has been revoked");
        }

        LocalDateTime now = LocalDateTime.now();

        if (preferencesRepository != null) {
            UserPreferences prefs = preferencesRepository.findByUser(session.userId());
            if (prefs.inactivityPolicy().exceededBy(session.lastSeenAt(), now)) {
                userSessionRepository.save(session.revoke());
                throw new SessionExpiredException("Session expired due to inactivity");
            }
        }

        User user = repository.findById(claims.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        RefreshTokenId newJti = RefreshTokenId.generate();
        UserSession updatedSession = session.withRotatedToken(newJti).touch(now);
        userSessionRepository.save(updatedSession);

        String newAccessToken = authProvider.generateAuthenticationToken(user, updatedSession.id());
        String newRefreshToken = authProvider.refreshAuthenticationToken(user, newJti, updatedSession.rememberMe());

        return new Session(user, newAccessToken, newRefreshToken);
    }
}
