package com.hasanipunchihewa.customer_tracker.repository;

import com.hasanipunchihewa.customer_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
