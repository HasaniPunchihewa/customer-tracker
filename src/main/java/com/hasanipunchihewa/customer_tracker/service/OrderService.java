package com.hasanipunchihewa.customer_tracker.service;

import com.hasanipunchihewa.customer_tracker.exception.NotFoundException;
import com.hasanipunchihewa.customer_tracker.model.Order;
import com.hasanipunchihewa.customer_tracker.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(UUID customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(UUID id, Order.Status status) {
        Order existing = getOrderById(id);
        existing.setStatus(status);
        return orderRepository.save(existing);
    }

    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }
}
