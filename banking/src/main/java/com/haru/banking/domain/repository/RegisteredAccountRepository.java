package com.haru.banking.domain.repository;

import com.haru.banking.domain.model.RegisteredBankAccount;

import java.util.Optional;
import java.util.UUID;

public interface RegisteredAccountRepository {
    Optional<RegisteredBankAccount> findByMemberId(UUID memberId);

    RegisteredBankAccount save(RegisteredBankAccount registeredAccount);
}
