package com.system.food_delivery_app.repository;


import com.restaurant.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {


Optional<Staff> findByEmail(String email);

List<Staff> findByName(String name);

@Query("SELECT s FROM Staff s WHERE s.role = 'STAFF'")
List<Staff> findAllStaff();

boolean existsByEmail(String email);


}