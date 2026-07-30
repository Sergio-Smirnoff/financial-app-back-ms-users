package com.financialapp.users.application;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserPreferencesRepository;
import com.financialapp.users.domain.usecase.GetUserPreferencesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserPreferencesUseCaseImpl implements GetUserPreferencesUseCase {

    private final UserPreferencesRepository repository;

    @Override
    public UserPreferences execute(UserId userId) {
        return repository.findByUser(userId);
    }
}
