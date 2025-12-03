package com.example.service;

import com.example.delivery.model.DeliveryModel;
import com.example.delivery.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class StaffDeliveryService {
    
    private final DeliveryRepository repository;

    @Autowired
    public StaffDeliveryService(DeliveryRepository repository) {
        this.repository = repository;
    }

    public List<DeliveryModel> getAssignedOrders() {
        return repository.findByStatusIn(Arrays.asList("Assigned", "In Transit"));
    }

    public Optional<DeliveryModel> updateOrderStatus(Integer orderId, String newStatus) {
        return repository.findById(orderId)
                .flatMap(order -> {
                    String currentStatus = order.getStatus();
                    
                    if (isValidDeliveryStatusTransition(currentStatus, newStatus)) {
                        order.setStatus(newStatus);
                        return Optional.of(repository.save(order));
                    }
                    return Optional.empty();
                });
    }

    private boolean isValidDeliveryStatusTransition(String currentStatus, String newStatus) {
        if ("In Transit".equalsIgnoreCase(currentStatus) && "Assigned".equalsIgnoreCase(newStatus)) {
            return false;
        }
        if ("Assigned".equalsIgnoreCase(currentStatus) && 
            ("In Transit".equalsIgnoreCase(newStatus) || "Delivered".equalsIgnoreCase(newStatus))) {
            return true;
        }
        if ("In Transit".equalsIgnoreCase(currentStatus) && "Delivered".equalsIgnoreCase(newStatus)) {
            return true;
        }
        return false;
    }
}