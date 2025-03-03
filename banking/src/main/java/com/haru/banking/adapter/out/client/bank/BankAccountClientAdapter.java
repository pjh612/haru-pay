package com.haru.banking.adapter.out.client.bank;


import com.haru.banking.appliation.client.BankAccountInfoClient;
import com.haru.banking.appliation.client.FirmBankingClient;
import com.haru.banking.appliation.client.dto.BankAccount;
import com.haru.banking.appliation.client.dto.ExternalFirmBankingRequest;
import com.haru.banking.appliation.client.dto.ExternalFirmBankingResponse;
import org.springframework.stereotype.Component;

@Component
public class BankAccountClientAdapter implements BankAccountInfoClient, FirmBankingClient {
    @Override
    public BankAccount getBankAccountInfo(String bankName, String bankAccountNo) {
        return new BankAccount(bankName, bankAccountNo, true);
    }

    @Override
    public ExternalFirmBankingResponse requestFirmBanking(ExternalFirmBankingRequest request) {
        return new ExternalFirmBankingResponse(1);
    }
}
