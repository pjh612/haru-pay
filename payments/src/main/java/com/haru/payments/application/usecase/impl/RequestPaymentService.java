package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.payments.adapter.in.event.PaymentRequestCreatedEvent;
import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.client.BankingClient;
import com.haru.payments.application.client.MemberClient;
import com.haru.payments.application.client.MoneyClient;
import com.haru.payments.application.client.dto.LoadMoneyResponse;
import com.haru.payments.application.client.dto.MemberResponse;
import com.haru.payments.application.client.dto.MoneyResponse;
import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;
import com.haru.payments.application.dto.*;
import com.haru.payments.application.event.PaymentRequestEvent;
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
import java.util.UUID;

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
    @CachePut(value = "paymentRequest", key = "#result.requestId", cacheManager = "cacheManager")
    @CacheEvict(value = "provisionalPayment", key = "#result.requestId", cacheManager = "cacheManager")
    public RequestPaymentResponse requestPayment(RequestPaymentCommand command) {
        PaymentResponse paymentRequest = paymentCacheRepository.findProvisionalPaymentById(command.paymentRequestId())
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        MemberResponse member = memberClient.getMemberById(command.requestMemberId());
        if (member == null) {
            throw new IllegalArgumentException("잘못된 유저 정보");
        }

        RegisteredBankAccountResponse registeredBankAccount = bankingClient.getRegisteredBankAccount(command.requestMemberId());
        if (registeredBankAccount == null) {
            throw new IllegalArgumentException("유효하지 않은 계좌");
        }

        MoneyResponse moneyResponse = moneyClient.getMemberById(command.requestMemberId());
        if (moneyResponse == null) {
            throw new IllegalArgumentException("머니 정보가 없습니다.");
        }

        if (moneyResponse.balance().compareTo(paymentRequest.requestPrice()) < 0) {
            BigDecimal shortage = paymentRequest.requestPrice().subtract(moneyResponse.balance());
            BigDecimal loadAmount = shortage.divide(BigDecimal.TEN.pow(4), RoundingMode.UP);
            loadAmount = loadAmount.setScale(0, RoundingMode.UP).multiply(BigDecimal.TEN.pow(4));

            LoadMoneyResponse loadMoneyResponse = moneyClient.loadMoney(command.requestMemberId(), loadAmount);
            if (!"SUCCEEDED".equals(loadMoneyResponse.status())) {
                throw new IllegalArgumentException("머니 충전에 실패했습니다.");
            }
        }

        return new RequestPaymentResponse(
                paymentRequest.requestId(),
                paymentRequest.orderId(),
                command.requestMemberId(),
                paymentRequest.productName(),
                paymentRequest.requestPrice(),
                paymentRequest.clientId(),
                paymentRequest.paymentStatus(),
                null
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "paymentRequest", key = "#command.paymentRequestId", cacheManager = "cacheManager")
    public void confirmPayment(PaymentCommand command) {
        RequestPaymentResponse paymentRequest = paymentCacheRepository.findPaymentRequestById(command.paymentRequestId())
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        PaymentRequestEvent event = new PaymentRequestEvent(paymentRequest.requestId(),
                paymentRequest.orderId(),
                paymentRequest.clientId(),
                paymentRequest.requestMemberId(),
                paymentRequest.productName(),
                paymentRequest.requestPrice());
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public PaymentResponse requestPayment(CreatePaymentRequest request) {
        PaymentRequest paymentRequest = PaymentRequest.createNew(request.requestId(),
                request.orderId(),
                request.requestMemberId(),
                request.productName(),
                request.requestPrice(),
                request.clientId());

        eventPublisher.publishEvent(PaymentRequestCreatedEvent.success(
                paymentRequest.getRequestId(),
                paymentRequest.getClientId(),
                paymentRequest.getRequestMemberId(),
                paymentRequest.getRequestPrice()));

        return PaymentResponse.of(paymentRequestRepository.save(paymentRequest));
    }

    @Override
    @Transactional
    public void failRequest(UUID requestId) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(requestId)
                .orElse(null);
        if (paymentRequest == null) {
            return;
        }
        paymentRequest.fail();

        paymentRequestRepository.save(paymentRequest);
        eventPublisher.publishEvent(PaymentRequestCreatedEvent.fail(
                null,
                paymentRequest.getClientId(),
                paymentRequest.getRequestMemberId(),
                paymentRequest.getRequestPrice()));
    }


}
