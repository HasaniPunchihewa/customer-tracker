package com.hasanipunchihewa.customer_tracker.repository;

import com.hasanipunchihewa.customer_tracker.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
