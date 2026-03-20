package com.haru.testclient.application.service;

import com.haru.testclient.adapter.out.client.PaymentsCommandClient;
import com.haru.testclient.application.dto.ConfirmPaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentResponse;
import com.haru.testclient.domain.model.MerchantSession;
import com.haru.testclient.domain.model.Order;
import com.haru.testclient.domain.model.OrderStatus;
import com.haru.testclient.domain.model.PreparedPayment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MerchantPaymentService {

    private final PaymentsCommandClient commandClient;
    private final OrderService orderService;

    public MerchantPaymentService(PaymentsCommandClient commandClient,
                                  OrderService orderService) {
        this.commandClient = commandClient;
        this.orderService = orderService;
    }

    public PreparedPayment preparePayment(MerchantSession session,
                                          String ownerUsername,
                                          String orderId,
                                          String productName,
                                          BigDecimal requestPrice,
                                          String idempotencyKey) {
        PreparePaymentRequest request = new PreparePaymentRequest(orderId, productName, requestPrice);

        PreparePaymentResponse response = commandClient.preparePayment(
                session.getClientId().toString(),
                session.getApiKey(),
                request,
                idempotencyKey
        );

        orderService.recordPrepared(
                orderId,
                ownerUsername,
                response.paymentId(),
                productName,
                requestPrice
        );

        return orderService.toPreparedPayment(orderService.getOwnedOrderByPaymentId(response.paymentId(), ownerUsername));
    }

    public void confirmPayment(MerchantSession session,
                               UUID paymentId,
                               String ownerUsername,
                               String idempotencyKey) {
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentId);
        commandClient.confirmPayment(
                session.getClientId().toString(),
                session.getApiKey(),
                request,
                idempotencyKey
        );

        updatePaymentStatus(paymentId, "CONFIRMING");
        orderService.markConfirming(paymentId, ownerUsername);
    }

    public PreparedPayment confirmRequestedPayment(MerchantSession session,
                                                   UUID paymentId,
                                                   String ownerUsername,
                                                   String orderId,
                                                   BigDecimal requestPrice,
                                                   String idempotencyKey) {
        Order order = orderService.getOwnedOrderByPaymentId(paymentId, ownerUsername);
        PreparedPayment payment = orderService.toPreparedPayment(order);

        if (!Objects.equals(payment.getOrderId(), orderId)) {
            throw new IllegalArgumentException("주문 정보가 일치하지 않습니다.");
        }

        if (payment.getRequestPrice().compareTo(requestPrice) != 0) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        if (order.getStatus() == OrderStatus.PAYMENT_COMPLETED || order.getStatus() == OrderStatus.PAYMENT_CONFIRMING) {
            return payment;
        }

        if (order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            throw new IllegalStateException("이미 실패한 결제입니다.");
        }

        orderService.verifyForConfirmation(paymentId, orderId, requestPrice, ownerUsername);

        confirmPayment(session, paymentId, ownerUsername, idempotencyKey);
        return orderService.toPreparedPayment(orderService.getOwnedOrderByPaymentId(paymentId, ownerUsername));
    }

    public List<PreparedPayment> getPayments(String ownerUsername) {
        return orderService.getPreparedPayments(ownerUsername);
    }

    public void updatePaymentStatus(UUID paymentId, String status) {
        orderService.syncByPaymentStatus(paymentId, status);
    }
}
