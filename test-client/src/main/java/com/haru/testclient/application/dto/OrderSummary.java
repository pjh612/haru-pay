package com.haru.testclient.application.dto;

import com.haru.testclient.domain.model.Order;
import com.haru.testclient.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderSummary(
        String orderId,
        UUID paymentId,
        String productName,
        BigDecimal orderAmount,
        OrderStatus status,
        Instant createdAt,
        Instant completedAt
) {
    public static OrderSummary from(Order order) {
        return new OrderSummary(
                order.getOrderId(),
                order.getPaymentId(),
                order.getProductName(),
                order.getOrderAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getCompletedAt()
        );
    }
}
