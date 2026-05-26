package com.hasanipunchihewa.customer_tracker.dto;

import lombok.Data;

@Data
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String instagramHandle;
    private String notes;
}
