package com.hasanipunchihewa.customer_tracker.controller;

import com.hasanipunchihewa.customer_tracker.dto.*;
import com.hasanipunchihewa.customer_tracker.model.Order;
import com.hasanipunchihewa.customer_tracker.model.OrderItem;
import com.hasanipunchihewa.customer_tracker.service.CustomerService;
import com.hasanipunchihewa.customer_tracker.service.OrderService;
import com.hasanipunchihewa.customer_tracker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id) {
        return toResponse(orderService.getOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getOrdersByCustomer(@PathVariable UUID customerId) {
        return orderService.getOrdersByCustomer(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        order.setCustomer(customerService.getCustomerById(request.getCustomerId()));
        order.setStatus(request.getStatus());
        order.setSource(request.getSource());
        order.setNotes(request.getNotes());

        List<OrderItem> items = request.getItems().stream().map(itemRequest -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(productService.getProductById(itemRequest.getProductId()));
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(productService.getProductById(itemRequest.getProductId()).getPrice());
            return item;
        }).toList();

        order.setOrderItems(items);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(orderService.createOrder(order)));
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable UUID id, @RequestParam Order.Status status) {
        return toResponse(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemResponses = o.getOrderItems() == null ? List.of() :
                o.getOrderItems().stream().map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build()).toList();

        return OrderResponse.builder()
                .id(o.getId())
                .customerId(o.getCustomer().getId())
                .customerName(o.getCustomer().getName())
                .status(o.getStatus())
                .source(o.getSource())
                .notes(o.getNotes())
                .items(itemResponses)
                .orderedAt(o.getOrderedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
