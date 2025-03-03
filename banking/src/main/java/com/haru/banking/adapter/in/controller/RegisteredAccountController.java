package com.haru.banking.adapter.in.controller;

import com.haru.banking.appliation.dto.QueryRegisteredBankAccountRequest;
import com.haru.banking.appliation.dto.QueryRegisteredBankAccountResponse;
import com.haru.banking.appliation.dto.RegisterAccountRequest;
import com.haru.banking.appliation.dto.RegisterBankAccountResponse;
import com.haru.banking.appliation.usecase.QueryRegisteredBankAccountUseCase;
import com.haru.banking.appliation.usecase.RegisterBankAccountUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/banking/account")
public class RegisteredAccountController {
    private final RegisterBankAccountUseCase registerAccountUseCase;
    private final QueryRegisteredBankAccountUseCase queryRegisteredBankAccountUseCase;

    @PostMapping
    public RegisterBankAccountResponse registerBankAccount(@RequestBody RegisterAccountRequest request) {
        return registerAccountUseCase.register(request);
    }

    @GetMapping
    public QueryRegisteredBankAccountResponse getBankAccount(@RequestParam UUID memberId) {
        return queryRegisteredBankAccountUseCase.query(new QueryRegisteredBankAccountRequest(memberId));
    }
}
