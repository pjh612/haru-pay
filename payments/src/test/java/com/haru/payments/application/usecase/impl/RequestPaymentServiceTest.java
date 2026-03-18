package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
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
import com.haru.payments.support.ContainerizedIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RequestPaymentServiceTest extends ContainerizedIntegrationTest {
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

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearPaymentCaches() {
        cacheManager.getCache("provisionalPayment").clear();
        cacheManager.getCache("provisionalPaymentIdempotency").clear();
    }

    @Test
    public void testConcurrentPaymentRequests() throws InterruptedException {
        UUID memberId = Generators.timeBasedEpochGenerator().generate();
        UUID orderId = Generators.timeBasedEpochGenerator().generate();
        UUID clientId = Generators.timeBasedEpochGenerator().generate();
        UUID registeredBankAccountId = Generators.timeBasedEpochGenerator().generate();
        String productName = "product-name";
        BigDecimal price = BigDecimal.valueOf(1000);

        PreparePaymentCommand preparePaymentCommand = new PreparePaymentCommand(clientId, orderId.toString(), price, productName, "prepare-key-1");
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

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentService.requestPayment(requestPaymentCommand);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        verify(moneyClient, times(1)).loadMoney(any(UUID.class), any(BigDecimal.class));
        assertThat(paymentRequestRepository.findById(paymentResponse.requestId())).isPresent();
    }

    @Test
    void preparePayment_ShouldReuseExistingProvisionalPayment_WhenIdempotencyKeyMatches() {
        UUID clientId = Generators.timeBasedEpochGenerator().generate();
        PreparePaymentCommand command = new PreparePaymentCommand(clientId, "ORDER-1", BigDecimal.valueOf(1000), "product-name", "prepare-key-1");

        PaymentResponse first = paymentService.preparePayment(command);
        PaymentResponse second = paymentService.preparePayment(command);

        assertThat(second.requestId()).isEqualTo(first.requestId());
    }

    @Test
    void preparePayment_ShouldRejectDifferentPayload_WhenIdempotencyKeyMatches() {
        UUID clientId = Generators.timeBasedEpochGenerator().generate();
        paymentService.preparePayment(new PreparePaymentCommand(clientId, "ORDER-1", BigDecimal.valueOf(1000), "product-name", "prepare-key-1"));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                paymentService.preparePayment(new PreparePaymentCommand(clientId, "ORDER-1", BigDecimal.valueOf(2000), "product-name", "prepare-key-1"))
        );
    }
}
