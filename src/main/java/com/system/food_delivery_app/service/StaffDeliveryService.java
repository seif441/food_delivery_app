package com.system.food_delivery_app.service;

import com.example.model.DeliveryStaff;
import com.example.repository.DeliveryStaffRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeliveryStaffService {
    private final DeliveryStaffRepository deliveryStaffRepository;

    public DeliveryStaffService(DeliveryStaffRepository deliveryStaffRepository) {
        this.deliveryStaffRepository = deliveryStaffRepository;
    }
    
    public DeliveryStaff createDeliveryStaff(DeliveryStaff newDeliveryStaff) {
        return deliveryStaffRepository.save(newDeliveryStaff);
    }
    
    public List<DeliveryStaff> getAllDeliveryStaff() {
        return deliveryStaffRepository.findAll();
    }
    
    public List<Long> viewAssignedOrders(Long deliveryStaffId) {
        return List.of(); 
    }

    public String updateDeliveryStatus(Long orderId, Long deliveryStaffId) {
        return ""; 
    }
}