package com.financialapp.users.domain.usecase;

import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.util.List;

public interface ListManualCurrencyRatesUseCase {
    List<ManualCurrencyRate> execute(UserId userId);
}
