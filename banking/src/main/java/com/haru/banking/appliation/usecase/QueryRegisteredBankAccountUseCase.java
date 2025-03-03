package com.haru.banking.appliation.usecase;

import com.haru.banking.appliation.dto.QueryRegisteredBankAccountRequest;
import com.haru.banking.appliation.dto.QueryRegisteredBankAccountResponse;

public interface QueryRegisteredBankAccountUseCase {
    QueryRegisteredBankAccountResponse query(QueryRegisteredBankAccountRequest request);

}
