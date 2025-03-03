package com.haru.banking.appliation.usecase.impl;

import com.haru.banking.appliation.dto.QueryRegisteredBankAccountRequest;
import com.haru.banking.appliation.dto.QueryRegisteredBankAccountResponse;
import com.haru.banking.appliation.usecase.QueryRegisteredBankAccountUseCase;
import com.haru.banking.domain.model.RegisteredBankAccount;
import com.haru.banking.domain.repository.RegisteredAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryRegisteredBankAccountByMemberIdService implements QueryRegisteredBankAccountUseCase {
    private final RegisteredAccountRepository repository;

    @Override
    public QueryRegisteredBankAccountResponse query(QueryRegisteredBankAccountRequest request) {
        RegisteredBankAccount registeredAccount = repository.findByMemberId(request.memberId())
                .orElseThrow(EntityNotFoundException::new);

        return new QueryRegisteredBankAccountResponse(
                registeredAccount.getId(),
                registeredAccount.getBankName(),
                registeredAccount.getAccountNumber()
        );
    }
}
