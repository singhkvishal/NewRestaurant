package com.example.restaurant.repository;

import com.example.restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByPaymentIntentId(String paymentIntentId);
    List<Order> findByEmailOrderByOrderTimeDesc(String email);
}
