package com.example.delivery.repository;

import com.example.delivery.model.DeliveryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryModel, Integer> {
    
    List<DeliveryModel> findByStatusIn(List<String> statuses);

}