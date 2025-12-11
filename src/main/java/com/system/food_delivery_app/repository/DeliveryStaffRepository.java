package com.system.food_delivery_app.repository;

import com.system.food_delivery_app.model.DeliveryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryStaffRepository extends JpaRepository<DeliveryStaff, Long> {

    // Crucial for System Auto-Assignment: Find first driver who is free
    Optional<DeliveryStaff> findFirstByIsAvailableTrue();

    Optional<DeliveryStaff> findByEmail(String email);

    // Helper for Admin to see all drivers
    @Query("SELECT d FROM DeliveryStaff d")
    List<DeliveryStaff> findAllDeliveryStaff();
}