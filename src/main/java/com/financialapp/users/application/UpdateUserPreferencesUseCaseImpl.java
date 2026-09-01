package com.financialapp.users.application;

import com.financialapp.users.domain.model.UserPreferences;
import com.financialapp.users.domain.model.valueObject.InactivityPolicy;
import com.financialapp.users.domain.repository.UserPreferencesRepository;
import com.financialapp.users.domain.usecase.UpdateUserPreferencesUseCase;
import com.financialapp.users.domain.usecase.command.UpdateUserPreferencesCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserPreferencesUseCaseImpl implements UpdateUserPreferencesUseCase {

    private final UserPreferencesRepository repository;

    @Override
    public UserPreferences execute(UpdateUserPreferencesCommand command) {
        UserPreferences existing = repository.findByUser(command.userId());

        InactivityPolicy policy = command.maxIdleMinutes() != null
                ? InactivityPolicy.fromMinutes(command.maxIdleMinutes())
                : existing.inactivityPolicy();

        String tz = command.timezone() != null ? command.timezone() : existing.timezone();
        String primary = command.primaryCurrency() != null ? command.primaryCurrency() : existing.primaryCurrency();
        String secondary = command.secondaryCurrency(); // nullable, explicit set
        String format = command.numberFormat() != null ? command.numberFormat() : existing.numberFormat();
        int decimals = command.decimals() != null ? command.decimals() : existing.decimals();
        boolean color = command.colorForAmounts() != null ? command.colorForAmounts() : existing.colorForAmounts();

        UserPreferences updated = new UserPreferences(
                command.userId(),
                policy,
                tz,
                primary,
                secondary,
                format,
                decimals,
                color
        );

        return repository.save(updated);
    }
}
