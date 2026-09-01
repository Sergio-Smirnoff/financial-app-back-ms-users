package com.financialapp.users.application;

import com.financialapp.users.domain.exception.SessionNotFoundException;
import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.RevokeUserSessionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RevokeUserSessionUseCaseImpl implements RevokeUserSessionUseCase {

    private final UserSessionRepository repository;

    @Override
    public void execute(SessionId sessionId, UserId userId) {
        UserSession session = repository.findByIdOwnedBy(sessionId, userId)
                .orElseThrow(SessionNotFoundException::new);
        repository.save(session.revoke());
    }
}
