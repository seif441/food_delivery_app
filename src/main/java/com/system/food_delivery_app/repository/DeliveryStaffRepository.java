package com.system.food_delivery_app.repository;

<<<<<<< HEAD:src/main/java/com/system/food_delivery_app/repository/DeliveryRepository.java

import com.restaurant.model.DeliveryStaff;
=======
import com.system.food_delivery_app.model.DeliveryStaff;
>>>>>>> 5c7cd28108648ea9d2662b36c833c41f66fda277:src/main/java/com/system/food_delivery_app/repository/DeliveryStaffRepository.java
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