package com.haru.testclient.application.service;

import com.haru.testclient.application.dto.OrderSummary;
import com.haru.testclient.domain.model.Order;
import com.haru.testclient.domain.model.OrderStatus;
import com.haru.testclient.domain.model.PreparedPayment;
import com.haru.testclient.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void recordPrepared(String orderId,
                               String ownerUsername,
                               UUID paymentId,
                               String productName,
                               BigDecimal orderAmount) {
        orderRepository.findByOrderIdAndOwnerUsername(orderId, ownerUsername)
                .ifPresentOrElse(
                        order -> order.updatePrepared(ownerUsername, paymentId, productName, orderAmount),
                        () -> orderRepository.save(Order.createPrepared(orderId, ownerUsername, paymentId, productName, orderAmount))
                );
    }

    @Transactional(readOnly = true)
    public Order verifyForConfirmation(UUID paymentId,
                                       String orderId,
                                       BigDecimal orderAmount,
                                       String ownerUsername) {
        Order order = orderRepository.findByPaymentIdAndOwnerUsername(paymentId, ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        if (!order.getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("주문 정보가 일치하지 않습니다.");
        }

        if (order.getOrderAmount().compareTo(orderAmount) != 0) {
            throw new IllegalArgumentException("주문 금액이 일치하지 않습니다.");
        }

        if (order.getStatus() != OrderStatus.PAYMENT_PREPARED) {
            throw new IllegalStateException("확정 가능한 주문 상태가 아닙니다.");
        }

        return order;
    }

    @Transactional
    public void markConfirming(UUID paymentId, String ownerUsername) {
        orderRepository.findByPaymentIdAndOwnerUsername(paymentId, ownerUsername)
                .ifPresent(Order::markConfirming);
    }

    @Transactional
    public void syncByPaymentStatus(UUID paymentId, String paymentStatus) {
        orderRepository.findByPaymentId(paymentId)
                .ifPresent(order -> {
                    if (order.getStatus() == OrderStatus.PAYMENT_COMPLETED || order.getStatus() == OrderStatus.PAYMENT_FAILED) {
                        return;
                    }

                    if ("SUCCEEDED".equals(paymentStatus)) {
                        order.markCompleted();
                        return;
                    }

                    if ("FAILED".equals(paymentStatus)) {
                        order.markFailed();
                    }
                });
    }

    @Transactional(readOnly = true)
    public List<OrderSummary> getOrders(String ownerUsername) {
        return orderRepository.findAllByOwnerUsernameOrderByCreatedAtDesc(ownerUsername)
                .stream()
                .map(OrderSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Order getOwnedOrderByPaymentId(UUID paymentId, String ownerUsername) {
        return orderRepository.findByPaymentIdAndOwnerUsername(paymentId, ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<PreparedPayment> getPreparedPayments(String ownerUsername) {
        return orderRepository.findAllByOwnerUsernameOrderByCreatedAtDesc(ownerUsername)
                .stream()
                .map(this::toPreparedPayment)
                .toList();
    }

    public PreparedPayment toPreparedPayment(Order order) {
        return new PreparedPayment(
                order.getPaymentId(),
                order.getOrderId(),
                order.getProductName(),
                order.getOrderAmount(),
                order.getCreatedAt(),
                mapOrderStatus(order.getStatus())
        );
    }

    private String mapOrderStatus(OrderStatus status) {
        if (status == null) {
            return "PREPARED";
        }

        return switch (status) {
            case PAYMENT_PREPARED -> "PREPARED";
            case PAYMENT_CONFIRMING -> "CONFIRMING";
            case PAYMENT_COMPLETED -> "SUCCEEDED";
            case PAYMENT_FAILED -> "FAILED";
        };
    }
}
