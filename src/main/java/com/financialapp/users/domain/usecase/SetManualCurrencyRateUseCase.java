package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.usecase.command.SetManualCurrencyRateCommand;

public interface SetManualCurrencyRateUseCase {
    ManualCurrencyRate execute(SetManualCurrencyRateCommand command);
}
