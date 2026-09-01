package com.financialapp.users.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.users.domain.model.ManualCurrencyRate;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.usecase.DeleteManualCurrencyRateUseCase;
import com.financialapp.users.domain.usecase.ListManualCurrencyRatesUseCase;
import com.financialapp.users.domain.usecase.SetManualCurrencyRateUseCase;
import com.financialapp.users.domain.usecase.command.DeleteManualCurrencyRateCommand;
import com.financialapp.users.domain.usecase.command.SetManualCurrencyRateCommand;
import com.financialapp.users.web.dto.request.SetManualCurrencyRateRequest;
import com.financialapp.users.web.dto.response.ManualCurrencyRateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/currency-rates")
@RequiredArgsConstructor
public class CurrencyRateController {

    private final ListManualCurrencyRatesUseCase listRatesUseCase;
    private final SetManualCurrencyRateUseCase setRateUseCase;
    private final DeleteManualCurrencyRateUseCase deleteRateUseCase;

    @GetMapping
    public ApiResponse<List<ManualCurrencyRateResponse>> listRates(@RequestHeader("X-User-Id") Long userId) {
        List<ManualCurrencyRate> rates = listRatesUseCase.execute(new UserId(userId));
        List<ManualCurrencyRateResponse> responseList = rates.stream()
                .map(ManualCurrencyRateResponse::fromDomain)
                .toList();
        return ApiResponse.ok("Manual currency rates retrieved", responseList);
    }

    @PutMapping("/{currency}")
    public ApiResponse<ManualCurrencyRateResponse> setRate(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String currency,
            @Valid @RequestBody SetManualCurrencyRateRequest request) {
        SetManualCurrencyRateCommand command = new SetManualCurrencyRateCommand(
                new UserId(userId),
                currency,
                request.ratePerArs()
        );
        ManualCurrencyRate rate = setRateUseCase.execute(command);
        return ApiResponse.ok("Manual currency rate updated", ManualCurrencyRateResponse.fromDomain(rate));
    }

    @DeleteMapping("/{currency}")
    public ApiResponse<Void> deleteRate(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String currency) {
        DeleteManualCurrencyRateCommand command = new DeleteManualCurrencyRateCommand(
                new UserId(userId),
                currency
        );
        deleteRateUseCase.execute(command);
        return ApiResponse.ok("Manual currency rate deleted", null);
    }
}
