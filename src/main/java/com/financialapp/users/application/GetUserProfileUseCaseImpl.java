package com.financialapp.users.application;

import com.financialapp.users.domain.exception.UserNotFoundException;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.domain.usecase.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final UserRepository repository;

    @Override
    public User execute(UserId userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
