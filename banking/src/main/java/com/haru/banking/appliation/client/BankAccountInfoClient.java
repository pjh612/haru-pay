package com.haru.banking.appliation.client;

import com.haru.banking.appliation.client.dto.BankAccount;

public interface BankAccountInfoClient {
    BankAccount getBankAccountInfo(String bankName, String bankAccountNo);
}
