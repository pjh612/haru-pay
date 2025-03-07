package com.haru.money.adapters.in.controller;

import com.haru.money.application.dto.LoadMoneyResponse;
import com.haru.money.application.dto.MoneyTransactionRequest;
import com.haru.money.application.usecase.LoadMoneyEdaUseCase;
import com.haru.money.application.usecase.LoadMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MoneyTransactionController {
    private final LoadMoneyEdaUseCase loadMoneyEdaUseCase;
    private final LoadMoneyUseCase loadMoneyUseCase;

    @PostMapping("/members/{memberId}/money/balance/async")
    public void loadMoneySaga(@PathVariable String memberId, @RequestBody MoneyTransactionRequest request) {
        loadMoneyEdaUseCase.loadMoneySaga(memberId, request.amount());
    }

    @PostMapping("/members/{memberId}/money/balance")
    public LoadMoneyResponse loadMoneySync(@PathVariable UUID memberId, @RequestBody MoneyTransactionRequest request) {
        return loadMoneyUseCase.loadMoney(memberId, request.amount());
    }
}
