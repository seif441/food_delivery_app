package com.system.food_delivery_app.repository;

import com.system.food_delivery_app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by email (used for login and profile lookup)
    Optional<Customer> findByEmail(String email);

    // Check if a customer already exists with this email (used in registration)
    boolean existsByEmail(String email);
}

