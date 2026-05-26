package com.hasanipunchihewa.customer_tracker.dto;

import com.hasanipunchihewa.customer_tracker.model.Order;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID customerId;
    private Order.Status status;
    private Order.Source source;
    private String notes;
    private List<OrderItemRequest> items;
}
