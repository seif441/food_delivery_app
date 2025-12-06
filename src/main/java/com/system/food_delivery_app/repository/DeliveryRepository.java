package com.system.food_delivery_app.repository;


import com.restaurant.model.DeliveryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryStaffRepository extends JpaRepository<DeliveryStaff, Long> {


Optional<DeliveryStaff> findByEmail(String email);

List<DeliveryStaff> findByName(String name);

@Query("SELECT d FROM DeliveryStaff d WHERE d.role = 'DELIVERY_STAFF'")
List<DeliveryStaff> findAllDeliveryStaff();

boolean existsByEmail(String email);


}