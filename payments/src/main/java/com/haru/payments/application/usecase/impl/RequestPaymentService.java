package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.common.lock.RedisLock;
import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.client.BankingClient;
import com.haru.payments.application.client.MemberClient;
import com.haru.payments.application.client.MoneyClient;
import com.haru.payments.application.client.dto.LoadMoneyResponse;
import com.haru.payments.application.client.dto.MemberResponse;
import com.haru.payments.application.client.dto.MoneyResponse;
import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;
import com.haru.payments.application.dto.*;
import com.haru.payments.application.event.ConfirmPaymentRequestEvent;
import com.haru.payments.application.usecase.RequestPaymentUseCase;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPaymentService implements RequestPaymentUseCase {
    private final MemberClient memberClient;
    private final BankingClient bankingClient;
    private final MoneyClient moneyClient;
    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentCacheRepository paymentCacheRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    @CachePut(value = "provisionalPayment", key = "#result.requestId", cacheManager = "cacheManager")
    public PaymentResponse preparePayment(PreparePaymentCommand command) {
        PaymentRequest paymentRequest = PaymentRequest.createNew(
                Generators.timeBasedEpochGenerator().generate(),
                command.orderId(),
                null,
                command.productName(),
                command.requestPrice(),
                command.clientId());

        return PaymentResponse.of(paymentRequest);
    }

    @Override
    @RedisLock(waitTime = 1, unit = TimeUnit.SECONDS, key = "#command.paymentRequestId")
    @CacheEvict(value = "provisionalPayment", key = "#result.requestId", cacheManager = "cacheManager")
    public RequestPaymentResponse requestPayment(RequestPaymentCommand command) {
        PaymentResponse paymentResponse = paymentCacheRepository.findProvisionalPaymentById(command.paymentRequestId())
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        PaymentRequest paymentRequest = PaymentRequest.createNew(paymentResponse.requestId(),
                paymentResponse.orderId(),
                command.requestMemberId(),
                paymentResponse.productName(),
                paymentResponse.requestPrice(),
                paymentResponse.clientId());


        validateMember(command, paymentRequest);
        validateBankAccount(command, paymentRequest);
        validateAndLoadMoneyIfNeeded(command, paymentRequest);

        paymentRequestRepository.save(paymentRequest);

        return new RequestPaymentResponse(
                paymentRequest.getRequestId(),
                paymentRequest.getOrderId(),
                command.requestMemberId(),
                paymentRequest.getProductName(),
                paymentRequest.getRequestPrice(),
                paymentRequest.getClientId(),
                paymentRequest.getPaymentStatus(),
                null
        );
    }

    private void validateMember(RequestPaymentCommand command, PaymentRequest paymentRequest) {
        MemberResponse member = memberClient.getMemberById(command.requestMemberId());
        if (member == null) {
            handleValidationFailure(paymentRequest, "잘못된 유저 정보");
        }
    }

    private void validateBankAccount(RequestPaymentCommand command, PaymentRequest paymentRequest) {
        RegisteredBankAccountResponse registeredBankAccount = bankingClient.getRegisteredBankAccount(command.requestMemberId());
        if (registeredBankAccount == null) {
            handleValidationFailure(paymentRequest, "유효하지 않은 계좌");
        }
    }

    private void validateAndLoadMoneyIfNeeded(RequestPaymentCommand command, PaymentRequest paymentRequest) {
        MoneyResponse moneyResponse = moneyClient.getMemberById(command.requestMemberId());
        if (moneyResponse == null) {
            handleValidationFailure(paymentRequest, "머니 정보가 없습니다.");
        }

        if (moneyResponse.balance().compareTo(paymentRequest.getRequestPrice()) < 0) {
            BigDecimal shortage = paymentRequest.getRequestPrice().subtract(moneyResponse.balance());
            BigDecimal loadAmount = calculateLoadAmount(shortage);

            LoadMoneyResponse loadMoneyResponse = moneyClient.loadMoney(command.requestMemberId(), loadAmount);
            if (!"SUCCEEDED".equals(loadMoneyResponse.status())) {
                handleValidationFailure(paymentRequest, "머니 충전에 실패했습니다.");
            }
        }
    }

    private BigDecimal calculateLoadAmount(BigDecimal shortage) {
        return shortage.divide(BigDecimal.TEN.pow(4), RoundingMode.UP)
                .setScale(0, RoundingMode.UP)
                .multiply(BigDecimal.TEN.pow(4));
    }

    private void handleValidationFailure(PaymentRequest paymentRequest, String errorMessage) {
        paymentRequest.fail();
        paymentRequestRepository.save(paymentRequest);
        throw new IllegalArgumentException(errorMessage);
    }

    @Override
    @Transactional
    @RedisLock(waitTime = 1, unit = TimeUnit.SECONDS, key = "#command.paymentRequestId")
    @CacheEvict(value = "paymentRequest", key = "#command.paymentRequestId", cacheManager = "cacheManager")
    public void confirmPayment(PaymentCommand command) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(command.paymentRequestId())
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        ConfirmPaymentRequestEvent event = new ConfirmPaymentRequestEvent(paymentRequest.getRequestId(), paymentRequest.getRequestMemberId(), paymentRequest.getRequestPrice());
        eventPublisher.publishEvent(event);
    }
}
