package com.system.food_delivery_app.repository;

<<<<<<< HEAD
import com.system.food_delivery_app.model.Staff;
=======

import com.example.model.Staff;
>>>>>>> c29c617705a343e594dd219d69c302c9ad04ea88
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
}