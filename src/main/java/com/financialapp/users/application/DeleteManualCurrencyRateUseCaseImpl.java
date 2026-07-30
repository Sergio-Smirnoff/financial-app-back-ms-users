package com.financialapp.users.application;

import com.financialapp.users.domain.repository.ManualCurrencyRateRepository;
import com.financialapp.users.domain.usecase.DeleteManualCurrencyRateUseCase;
import com.financialapp.users.domain.usecase.command.DeleteManualCurrencyRateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteManualCurrencyRateUseCaseImpl implements DeleteManualCurrencyRateUseCase {

    private final ManualCurrencyRateRepository repository;

    @Override
    public void execute(DeleteManualCurrencyRateCommand command) {
        repository.deleteByUserAndCurrency(command.userId(), command.currency());
    }
}
