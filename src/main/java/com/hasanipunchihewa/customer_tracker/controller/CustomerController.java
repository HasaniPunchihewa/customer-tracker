package com.hasanipunchihewa.customer_tracker.controller;

import com.hasanipunchihewa.customer_tracker.dto.CustomerRequest;
import com.hasanipunchihewa.customer_tracker.dto.CustomerResponse;
import com.hasanipunchihewa.customer_tracker.model.Customer;
import com.hasanipunchihewa.customer_tracker.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable UUID id) {
        return toResponse(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setInstagramHandle(request.getInstagramHandle());
        customer.setNotes(request.getNotes());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(customerService.createCustomer(customer)));
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequest request) {
        Customer updated = new Customer();
        updated.setName(request.getName());
        updated.setEmail(request.getEmail());
        updated.setPhone(request.getPhone());
        updated.setAddress(request.getAddress());
        updated.setInstagramHandle(request.getInstagramHandle());
        updated.setNotes(request.getNotes());

        return toResponse(customerService.updateCustomer(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .instagramHandle(c.getInstagramHandle())
                .notes(c.getNotes())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
