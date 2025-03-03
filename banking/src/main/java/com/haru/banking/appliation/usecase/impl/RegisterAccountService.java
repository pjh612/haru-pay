package com.haru.banking.appliation.usecase.impl;

import com.haru.banking.appliation.client.BankAccountInfoClient;
import com.haru.banking.appliation.client.MemberClient;
import com.haru.banking.appliation.client.dto.BankAccount;
import com.haru.banking.appliation.client.dto.FindMemberResponse;
import com.haru.banking.appliation.dto.RegisterAccountRequest;
import com.haru.banking.appliation.dto.RegisterBankAccountResponse;
import com.haru.banking.appliation.usecase.RegisterBankAccountUseCase;
import com.haru.banking.domain.model.RegisteredBankAccount;
import com.haru.banking.domain.repository.RegisteredAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAccountService implements RegisterBankAccountUseCase {
    private final RegisteredAccountRepository registeredAccountRepository;
    private final MemberClient memberClient;
    private final BankAccountInfoClient bankAccountInfoClient;

    @Override
    public RegisterBankAccountResponse register(RegisterAccountRequest request) {
        //멤버 확인
        FindMemberResponse memberResponse = memberClient.findByMemberId(request.memberId());
        if(memberResponse == null) {
            throw new IllegalArgumentException("유효하지 않은 회원입니다.");
        }

        //계좌 정보 확인
        BankAccount bankAccountInfo = bankAccountInfoClient.getBankAccountInfo(request.bankName(), request.bankAccountNumber());
        boolean isValid = bankAccountInfo.isValid();

        if (isValid) {
            RegisteredBankAccount registeredAccount = RegisteredBankAccount.createNew(
                    request.memberId(),
                    request.bankName(),
                    request.bankAccountNumber(),
                    true
            );
            RegisteredBankAccount save = registeredAccountRepository.save(registeredAccount);

            return new RegisterBankAccountResponse(save.getId());
        } else {
            throw new IllegalArgumentException("유효하지 않은 계좌");
        }
    }
}
