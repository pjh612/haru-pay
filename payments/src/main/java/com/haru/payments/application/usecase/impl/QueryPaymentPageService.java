package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.client.dto.MoneyResponse;
import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;
import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.PaymentPageResponse;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.port.out.client.BankingClient;
import com.haru.payments.application.port.out.client.MoneyClient;
import com.haru.payments.application.usecase.QueryClientUseCase;
import com.haru.payments.application.usecase.QueryPaymentPageUseCase;
import com.haru.payments.application.usecase.QueryPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryPaymentPageService implements QueryPaymentPageUseCase {
    private final QueryPaymentUseCase queryPaymentUseCase;
    private final QueryClientUseCase queryClientUseCase;
    private final MoneyClient moneyClient;
    private final BankingClient bankingClient;

    @Override
    public PaymentPageResponse query(UUID paymentRequestId, UUID memberId) {
        PaymentResponse payment = queryPaymentUseCase.queryById(paymentRequestId);
        ClientResponse client = queryClientUseCase.queryById(payment.clientId());
        MoneyResponse money = moneyClient.getMemberById(memberId);
        RegisteredBankAccountResponse bankAccount = bankingClient.getRegisteredBankAccount(memberId);

        BigDecimal shortage = payment.requestPrice().subtract(money.balance());
        BigDecimal shortfallAmount = shortage.divide(BigDecimal.TEN.pow(4), RoundingMode.UP)
                .setScale(0, RoundingMode.UP)
                .multiply(BigDecimal.TEN.pow(4));

        return new PaymentPageResponse(
                payment.requestId(),
                payment.orderId(),
                payment.requestPrice(),
                client.name(),
                money.balance(),
                shortfallAmount,
                bankAccount.id(),
                bankAccount.bankName(),
                bankAccount.accountNumber()
        );
    }
}
