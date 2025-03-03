package com.haru.money.adapters.in.controller;

import com.haru.money.application.dto.CreateMoneyRequest;
import com.haru.money.application.dto.MoneyInfoResponse;
import com.haru.money.application.usecase.CreateMoneyUseCase;
import com.haru.money.application.usecase.QueryMoneyByMemberIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MoneyController {
    private final QueryMoneyByMemberIdUseCase queryMoneyByMemberSeqUseCase;
    private final CreateMoneyUseCase createMoneyUseCase;

    @GetMapping("/money")
    public MoneyInfoResponse getMoneyInfo(@RequestParam UUID memberId) {
        return queryMoneyByMemberSeqUseCase.query(memberId);
    }

    @PostMapping("/money")
    public UUID createJoyMoney(@RequestBody CreateMoneyRequest request) {
        return createMoneyUseCase.create(request);
    }
}
