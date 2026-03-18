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
import com.haru.payments.domain.model.PaymentConfirmIdempotency;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentConfirmIdempotencyRepository;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPaymentService implements RequestPaymentUseCase {
    private final MemberClient memberClient;
    private final BankingClient bankingClient;
    private final MoneyClient moneyClient;
    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentConfirmIdempotencyRepository paymentConfirmIdempotencyRepository;
    private final PaymentCacheRepository paymentCacheRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public PaymentResponse preparePayment(PreparePaymentCommand command) {
        String normalizedIdempotencyKey = normalizeIdempotencyKey(command.idempotencyKey());

        if (normalizedIdempotencyKey != null) {
            return paymentCacheRepository.findProvisionalPaymentByIdempotency(command.clientId(), normalizedIdempotencyKey)
                    .map(cached -> validateAndReusePreparedPayment(command, cached))
                    .orElseGet(() -> createAndCachePreparedPayment(command, normalizedIdempotencyKey));
        }

        return createAndCachePreparedPayment(command, null);
    }

    private PaymentResponse createAndCachePreparedPayment(PreparePaymentCommand command, String idempotencyKey) {
        PaymentRequest paymentRequest = PaymentRequest.createNew(
                Generators.timeBasedEpochGenerator().generate(),
                command.orderId(),
                null,
                command.productName(),
                command.requestPrice(),
                command.clientId());

        PaymentResponse response = new PaymentResponse(
                paymentRequest.getRequestId(),
                paymentRequest.getOrderId(),
                paymentRequest.getRequestMemberId(),
                paymentRequest.getProductName(),
                paymentRequest.getRequestPrice(),
                paymentRequest.getClientId(),
                paymentRequest.getPaymentStatus(),
                paymentRequest.getApprovedAt(),
                idempotencyKey
        );

        paymentCacheRepository.saveProvisionalPayment(response);
        if (idempotencyKey != null) {
            paymentCacheRepository.saveProvisionalPaymentByIdempotency(command.clientId(), idempotencyKey, response);
        }

        return response;
    }

    private PaymentResponse validateAndReusePreparedPayment(PreparePaymentCommand command, PaymentResponse cached) {
        if (!cached.orderId().equals(command.orderId())) {
            throw new IllegalArgumentException("동일 멱등성 키로 다른 주문을 요청할 수 없습니다.");
        }

        if (!cached.productName().equals(command.productName())) {
            throw new IllegalArgumentException("동일 멱등성 키로 다른 상품을 요청할 수 없습니다.");
        }

        if (cached.requestPrice().compareTo(command.requestPrice()) != 0) {
            throw new IllegalArgumentException("동일 멱등성 키로 다른 금액을 요청할 수 없습니다.");
        }

        return cached;
    }

    @Override
    @RedisLock(waitTime = 1, unit = TimeUnit.SECONDS, key = "#command.paymentRequestId")
    public RequestPaymentResponse requestPayment(RequestPaymentCommand command) {
        PaymentResponse paymentResponse = paymentCacheRepository.findProvisionalPaymentById(command.paymentRequestId())
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        evictProvisionalPayment(paymentResponse);

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
        String normalizedIdempotencyKey = normalizeIdempotencyKey(command.idempotencyKey());

        if (normalizedIdempotencyKey != null && replayIfConfirmAlreadyAccepted(command, normalizedIdempotencyKey)) {
            return;
        }

        PaymentRequest paymentRequest = paymentRequestRepository.findByIdAndClientId(command.paymentRequestId(), command.clientId())
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        if (paymentRequest.isSucceeded()) {
            log.info("Skipping duplicate confirm request for paymentId={}", command.paymentRequestId());
            return;
        }

        if (paymentRequest.isFailed()) {
            throw new IllegalStateException("이미 실패한 결제입니다.");
        }

        if (normalizedIdempotencyKey != null) {
            if (persistConfirmIdempotency(command, normalizedIdempotencyKey)) {
                return;
            }
        }

        ConfirmPaymentRequestEvent event = new ConfirmPaymentRequestEvent(paymentRequest.getRequestId(), paymentRequest.getRequestMemberId(), paymentRequest.getRequestPrice());
        eventPublisher.publishEvent(event);
    }

    private void evictProvisionalPayment(PaymentResponse paymentResponse) {
        paymentCacheRepository.evictProvisionalPayment(paymentResponse.requestId());

        String idempotencyKey = normalizeIdempotencyKey(paymentResponse.idempotencyKey());
        if (idempotencyKey != null) {
            paymentCacheRepository.evictProvisionalPaymentByIdempotency(paymentResponse.clientId(), idempotencyKey);
        }
    }

    private void ensureSamePayment(UUID requestedPaymentId, UUID cachedPaymentId) {
        if (!cachedPaymentId.equals(requestedPaymentId)) {
            throw new IllegalArgumentException("동일 멱등성 키로 다른 결제를 확정할 수 없습니다.");
        }
    }

    private boolean replayIfConfirmAlreadyAccepted(PaymentCommand command, String idempotencyKey) {
        return paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(command.clientId(), idempotencyKey)
                .map(existing -> {
                    ensureSamePayment(command.paymentRequestId(), existing.getPaymentId());
                    log.info("Skipping duplicate confirm request for paymentId={} with idempotencyKey={}", command.paymentRequestId(), idempotencyKey);
                    return true;
                })
                .orElse(false);
    }

    private boolean persistConfirmIdempotency(PaymentCommand command, String idempotencyKey) {
        try {
            paymentConfirmIdempotencyRepository.save(PaymentConfirmIdempotency.createNew(command.clientId(), idempotencyKey, command.paymentRequestId()));
            return false;
        } catch (DataIntegrityViolationException e) {
            PaymentConfirmIdempotency existing = paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(command.clientId(), idempotencyKey)
                    .orElseThrow(() -> e);
            ensureSamePayment(command.paymentRequestId(), existing.getPaymentId());
            log.info("Skipping duplicate confirm request for paymentId={} with idempotencyKey={} after unique-key collision", command.paymentRequestId(), idempotencyKey);
            return true;
        }
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null) {
            return null;
        }

        String trimmed = idempotencyKey.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
