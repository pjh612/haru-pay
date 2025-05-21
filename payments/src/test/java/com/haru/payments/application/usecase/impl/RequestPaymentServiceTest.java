package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.client.BankingClient;
import com.haru.payments.application.client.MemberClient;
import com.haru.payments.application.client.MoneyClient;
import com.haru.payments.application.client.dto.LoadMoneyResponse;
import com.haru.payments.application.client.dto.MemberResponse;
import com.haru.payments.application.client.dto.MoneyResponse;
import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.PreparePaymentCommand;
import com.haru.payments.application.dto.RequestPaymentCommand;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RequestPaymentServiceTest {
    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @MockitoBean
    private MemberClient memberClient;

    @MockitoBean
    private BankingClient bankingClient;

    @MockitoBean
    private MoneyClient moneyClient;

    @Autowired
    private RequestPaymentService paymentService;

    @Test
    @Transactional
    public void testConcurrentPaymentRequests() throws InterruptedException {
        // Given
        UUID memberId = Generators.timeBasedEpochGenerator().generate();
        UUID orderId = Generators.timeBasedEpochGenerator().generate();
        UUID clientId = Generators.timeBasedEpochGenerator().generate();
        UUID registeredBankAccountId = Generators.timeBasedEpochGenerator().generate();
        String productName = "product-name";
        BigDecimal price = BigDecimal.valueOf(1000);

        PreparePaymentCommand preparePaymentCommand = new PreparePaymentCommand(clientId, orderId.toString(), price, productName);
        PaymentResponse paymentResponse = paymentService.preparePayment(preparePaymentCommand);
        RequestPaymentCommand requestPaymentCommand = new RequestPaymentCommand(paymentResponse.requestId(), memberId);
        LoadMoneyResponse loadMoneyResponse = new LoadMoneyResponse(UUID.randomUUID(), BigDecimal.valueOf(10000), "SUCCEEDED", Instant.now());

        given(memberClient.getMemberById(memberId))
                .willReturn(new MemberResponse(memberId.toString(), "username", null, "name", "M"));

        given(bankingClient.getRegisteredBankAccount(memberId))
                .willReturn(new RegisteredBankAccountResponse(registeredBankAccountId.toString(), "bank-name", "123-123-123-123"));

        given(moneyClient.getMemberById(memberId))
                .willReturn(new MoneyResponse(memberId, BigDecimal.valueOf(500)));

        given(moneyClient.loadMoney(any(UUID.class), any(BigDecimal.class))).willReturn(loadMoneyResponse);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentService.requestPayment(requestPaymentCommand);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        verify(moneyClient, times(1)).loadMoney(any(UUID.class), any(BigDecimal.class));

        assertThat(paymentRequestRepository.findById(paymentResponse.requestId())).isPresent();
    }
}