package com.financialapp.users.application;

import com.financialapp.users.domain.model.UserSession;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserSessionRepository;
import com.financialapp.users.domain.usecase.ListUserSessionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListUserSessionsUseCaseImpl implements ListUserSessionsUseCase {

    private final UserSessionRepository repository;

    @Override
    public List<UserSession> execute(UserId userId) {
        return repository.findByUser(userId);
    }
}
