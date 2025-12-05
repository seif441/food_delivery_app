package com.system.food_delivery_app.repository;

import com.example.model.DeliveryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryStaffRepository extends JpaRepository<DeliveryStaff, Long> {
}