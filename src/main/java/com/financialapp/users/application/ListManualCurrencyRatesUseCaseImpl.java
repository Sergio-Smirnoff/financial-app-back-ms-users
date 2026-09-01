package com.financialapp.users.application;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.ManualCurrencyRateRepository;
import com.financialapp.users.domain.usecase.ListManualCurrencyRatesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListManualCurrencyRatesUseCaseImpl implements ListManualCurrencyRatesUseCase {

    private final ManualCurrencyRateRepository repository;

    @Override
    public List<ManualCurrencyRate> execute(UserId userId) {
        return repository.findByUser(userId);
    }
}
