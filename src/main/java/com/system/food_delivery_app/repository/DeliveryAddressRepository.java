package com.deliveryapp.demo.repository;

import com.example.fooddelivery.model.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
     * @param userId T
    @return 
    List<DeliveryAddress> findByUserId(Long userId);
}
