package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.PayloadType;
import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.model.SagaStatus;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaStateRecoveryService {
    private static final String PAYMENT_CONFIRM_REQUEST = "payment-confirm-request";
    private static final String DECREASE_MONEY = "decrease-money";
    private static final String CONFIRM_PAYMENT = "confirm-payment";
    private static final String LOAD_MONEY_REQUEST = "load-money-request";
    private static final String VALIDATE_BANK_ACCOUNT = "validate-bank-account";
    private static final String REQUEST_FIRM_BANKING = "request-firm-banking";
    private static final String LOAD_MONEY = "load-money";

    private final SagaStateRepository sagaStateRepository;
    private final SagaRecoveryExecutor sagaRecoveryExecutor;
    private final SagaRecoveryProperties recoveryProperties;
    private final Clock clock;

    @Scheduled(
            fixedDelayString = "${saga.recovery.scan-interval:PT1M}",
            initialDelayString = "${saga.recovery.scan-interval:PT1M}"
    )
    public void recoverStaleSagas() {
        if (!recoveryProperties.isEnabled()) {
            return;
        }

        recoverStartedStep(PAYMENT_CONFIRM_REQUEST, recoveryProperties.getPaymentConfirmRequestTimeout(), PayloadType.REQUEST,
                "Retrying stale first-step payment request is safe because it only re-emits the orchestration request.");
        recoverStartedStep(CONFIRM_PAYMENT, recoveryProperties.getConfirmPaymentTimeout(), PayloadType.REQUEST,
                "Retrying stale confirm-payment is allowed because payment confirmation is terminal-state guarded.");
        markManualReviewForStartedStep(DECREASE_MONEY, recoveryProperties.getDecreaseMoneyTimeout(),
                "Stale decrease-money requires reconciliation before retry to avoid incorrect debit rollback.");

        recoverAbortingStep(PAYMENT_CONFIRM_REQUEST, recoveryProperties.getAbortingTimeout(), PayloadType.CANCEL,
                "Retrying stale compensation for payment-confirm-request is safe because failConfirm is terminal-state guarded.");
        markManualReviewForAbortingStep(DECREASE_MONEY, recoveryProperties.getAbortingTimeout(),
                "Stale decrease-money compensation requires manual review/reconciliation.");

        markManualReviewForStartedStep(LOAD_MONEY_REQUEST, recoveryProperties.getLoadMoneyTimeout(),
                "Stale load-money saga step requires manual review until deterministic recovery is implemented.");
        markManualReviewForStartedStep(VALIDATE_BANK_ACCOUNT, recoveryProperties.getLoadMoneyTimeout(),
                "Stale bank-account validation step requires manual review until deterministic recovery is implemented.");
        markManualReviewForStartedStep(REQUEST_FIRM_BANKING, recoveryProperties.getLoadMoneyTimeout(),
                "Stale firm-banking step requires manual review until deterministic recovery is implemented.");
        markManualReviewForStartedStep(LOAD_MONEY, recoveryProperties.getLoadMoneyTimeout(),
                "Stale load-money completion step requires manual review until deterministic recovery is implemented.");

        markManualReviewForAbortingStep(LOAD_MONEY_REQUEST, recoveryProperties.getAbortingTimeout(),
                "Stale load-money compensation requires manual review until deterministic recovery is implemented.");
        markManualReviewForAbortingStep(VALIDATE_BANK_ACCOUNT, recoveryProperties.getAbortingTimeout(),
                "Stale validate-bank-account compensation requires manual review until deterministic recovery is implemented.");
        markManualReviewForAbortingStep(REQUEST_FIRM_BANKING, recoveryProperties.getAbortingTimeout(),
                "Stale request-firm-banking compensation requires manual review until deterministic recovery is implemented.");
        markManualReviewForAbortingStep(LOAD_MONEY, recoveryProperties.getAbortingTimeout(),
                "Stale load-money compensation requires manual review until deterministic recovery is implemented.");
    }

    private void recoverStartedStep(String currentStep, java.time.Duration timeout, PayloadType payloadType, String reason) {
        recover(findStaleSagas(SagaStatus.STARTED, currentStep, timeout), payloadType, reason);
    }

    private void recoverAbortingStep(String currentStep, java.time.Duration timeout, PayloadType payloadType, String reason) {
        recover(findStaleSagas(SagaStatus.ABORTING, currentStep, timeout), payloadType, reason);
    }

    private void markManualReviewForStartedStep(String currentStep, java.time.Duration timeout, String reason) {
        markManualReview(findStaleSagas(SagaStatus.STARTED, currentStep, timeout), reason);
    }

    private void markManualReviewForAbortingStep(String currentStep, java.time.Duration timeout, String reason) {
        markManualReview(findStaleSagas(SagaStatus.ABORTING, currentStep, timeout), reason);
    }

    private List<SagaState> findStaleSagas(SagaStatus sagaStatus, String currentStep, java.time.Duration timeout) {
        Instant cutoff = Instant.now(clock).minus(timeout);
        return sagaStateRepository.findBySagaStatusAndCurrentStepAndLastProgressAtBefore(sagaStatus, currentStep, cutoff);
    }

    private void recover(List<SagaState> staleSagas, PayloadType payloadType, String reason) {
        for (SagaState staleSaga : staleSagas) {
            try {
                log.warn("Recovering stale saga. sagaId={}, currentStep={}, sagaStatus={}, reason={}",
                        staleSaga.getId(), staleSaga.getCurrentStep(), staleSaga.getSagaStatus(), reason);
                sagaRecoveryExecutor.redrive(staleSaga.getId(), payloadType, recoveryProperties.getMaxRecoveryAttempts());
            } catch (Exception e) {
                log.error("Failed to recover stale saga. sagaId={}, currentStep={}", staleSaga.getId(), staleSaga.getCurrentStep(), e);
            }
        }
    }

    private void markManualReview(List<SagaState> staleSagas, String reason) {
        for (SagaState staleSaga : staleSagas) {
            try {
                sagaRecoveryExecutor.markManualReview(staleSaga.getId(), reason);
            } catch (Exception e) {
                log.error("Failed to mark stale saga for manual review. sagaId={}, currentStep={}", staleSaga.getId(), staleSaga.getCurrentStep(), e);
            }
        }
    }
}
