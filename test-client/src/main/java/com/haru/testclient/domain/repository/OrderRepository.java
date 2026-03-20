package com.haru.testclient.domain.repository;

import com.haru.testclient.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderIdAndOwnerUsername(String orderId, String ownerUsername);

    Optional<Order> findByPaymentIdAndOwnerUsername(UUID paymentId, String ownerUsername);

    Optional<Order> findByPaymentId(UUID paymentId);

    List<Order> findAllByOwnerUsernameOrderByCreatedAtDesc(String ownerUsername);
}
