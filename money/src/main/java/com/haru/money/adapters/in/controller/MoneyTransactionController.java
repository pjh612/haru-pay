package com.haru.money.adapters.in.controller;

import com.haru.money.application.dto.MoneyTransactionRequest;
import com.haru.money.application.usecase.LoadMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MoneyTransactionController {
    private final LoadMoneyUseCase loadMoneyUseCase;

    @PostMapping("/members/{memberId}/money/balance")
    public void loadMoneySaga(@PathVariable String memberId, @RequestBody MoneyTransactionRequest request) {
        loadMoneyUseCase.loadMoneySaga(memberId, request.amount());
    }
}
