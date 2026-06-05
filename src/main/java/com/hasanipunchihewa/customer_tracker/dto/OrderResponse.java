package com.hasanipunchihewa.customer_tracker.dto;

import com.hasanipunchihewa.customer_tracker.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private Order.Status status;
    private Order.Source source;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime orderedAt;
    private LocalDateTime updatedAt;
}
