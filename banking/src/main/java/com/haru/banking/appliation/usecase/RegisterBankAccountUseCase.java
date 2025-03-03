package com.haru.banking.appliation.usecase;

import com.haru.banking.appliation.dto.RegisterAccountRequest;
import com.haru.banking.appliation.dto.RegisterBankAccountResponse;

public interface RegisterBankAccountUseCase {
    RegisterBankAccountResponse register(RegisterAccountRequest request);
}
