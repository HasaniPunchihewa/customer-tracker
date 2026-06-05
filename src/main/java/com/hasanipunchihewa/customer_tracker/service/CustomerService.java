package com.hasanipunchihewa.customer_tracker.service;

import com.hasanipunchihewa.customer_tracker.exception.NotFoundException;
import com.hasanipunchihewa.customer_tracker.model.Customer;
import com.hasanipunchihewa.customer_tracker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(UUID id, Customer updated) {
        Customer existing = getCustomerById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setInstagramHandle(updated.getInstagramHandle());
        existing.setNotes(updated.getNotes());
        return customerRepository.save(existing);
    }

    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }
}
