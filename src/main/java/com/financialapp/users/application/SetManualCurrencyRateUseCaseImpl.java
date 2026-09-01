package com.financialapp.users.application;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.repository.ManualCurrencyRateRepository;
import com.financialapp.users.domain.usecase.SetManualCurrencyRateUseCase;
import com.financialapp.users.domain.usecase.command.SetManualCurrencyRateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SetManualCurrencyRateUseCaseImpl implements SetManualCurrencyRateUseCase {

    private final ManualCurrencyRateRepository repository;

    @Override
    public ManualCurrencyRate execute(SetManualCurrencyRateCommand command) {
        ManualCurrencyRate rate = new ManualCurrencyRate(
                command.userId(),
                command.currency(),
                command.ratePerArs(),
                LocalDateTime.now()
        );
        return repository.save(rate);
    }
}
