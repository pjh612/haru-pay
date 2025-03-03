package com.haru.banking.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisteredBankAccount {
    private UUID id;
    private UUID memberId;
    private String bankName;
    private String accountNumber;
    private boolean isValid;

    public static RegisteredBankAccount createNew(UUID memberId, String bankName, String accountNumber, boolean isValid) {
        return new RegisteredBankAccount(null, memberId, bankName, accountNumber, isValid);
    }
}
