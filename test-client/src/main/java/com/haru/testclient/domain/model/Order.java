package com.haru.testclient.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "client_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String ownerUsername;

    @Column(nullable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant completedAt;

    protected Order() {
    }

    public static Order createPrepared(String orderId,
                                       String ownerUsername,
                                       UUID paymentId,
                                       String productName,
                                       BigDecimal orderAmount) {
        Order order = new Order();
        order.orderId = orderId;
        order.ownerUsername = ownerUsername;
        order.paymentId = paymentId;
        order.productName = productName;
        order.orderAmount = orderAmount;
        order.status = OrderStatus.PAYMENT_PREPARED;
        return order;
    }

    public void updatePrepared(String ownerUsername, UUID paymentId, String productName, BigDecimal orderAmount) {
        this.ownerUsername = ownerUsername;
        this.paymentId = paymentId;
        this.productName = productName;
        this.orderAmount = orderAmount;
        this.status = OrderStatus.PAYMENT_PREPARED;
        this.completedAt = null;
    }

    public void markConfirming() {
        this.status = OrderStatus.PAYMENT_CONFIRMING;
    }

    public void markCompleted() {
        this.status = OrderStatus.PAYMENT_COMPLETED;
        this.completedAt = Instant.now();
    }

    public void markFailed() {
        this.status = OrderStatus.PAYMENT_FAILED;
        this.completedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
