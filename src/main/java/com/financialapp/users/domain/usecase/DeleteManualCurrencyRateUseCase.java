package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.usecase.command.DeleteManualCurrencyRateCommand;

public interface DeleteManualCurrencyRateUseCase {
    void execute(DeleteManualCurrencyRateCommand command);
}
