package com.hasanipunchihewa.customer_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String instagramHandle;
    private String notes;
    private LocalDateTime createdAt;
}
