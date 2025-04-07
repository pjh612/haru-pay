package com.haru.payments.adapter.out.alert;

import com.alert.core.messaging.broadcaster.MessageConverter;
import com.alert.core.messaging.model.AlertMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.payments.application.usecase.dto.PaymentConfirmResponse;

public class ConfirmPaymentAlertFormatter implements MessageConverter<AlertMessage, String> {
    private final ObjectMapper objectMapper;

    public ConfirmPaymentAlertFormatter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convert(AlertMessage message) {
        PaymentConfirmResponse confirm = objectMapper.convertValue(message.body(), PaymentConfirmResponse.class);

        return String.format("""
            ✅ [결제 승인 완료]

            • 요청 ID: %s
            • 주문 ID: %s
            • 회원 ID: %s
            • 요청 금액: %,.0f원
            • 클라이언트 ID: %s
            • 결제 상태: %d
            • 실패 사유: %s
            • 승인 시각: %s
            """,
                confirm.requestId(),
                confirm.orderId(),
                confirm.requestMemberId(),
                confirm.requestPrice(),
                confirm.clientId(),
                confirm.paymentStatus(),
                confirm.failureReason() == null ? "(없음)" : confirm.failureReason(),
                confirm.approvedAt()
        );
    }
}
