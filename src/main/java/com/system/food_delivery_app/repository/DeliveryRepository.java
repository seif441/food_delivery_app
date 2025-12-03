package com.system.food_delivery_app.repository;

import com.system.food_delivery_app.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    List<Delivery> findByStatusIn(List<String> statuses);

}