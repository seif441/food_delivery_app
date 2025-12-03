package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {
    
    private final StaffRepository repository;

    @Autowired
    public StaffService(StaffRepository repository) {
        this.repository = repository;
    }

    public List<Staff> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Staff> prepareOrder(Integer orderId) {
        return repository.findById(orderId)
                .flatMap(order -> {
                    if ("Pending".equalsIgnoreCase(order.getStatus())) {
                        order.setStatus("Preparing");
                        return Optional.of(repository.save(order));
                    }
                    return Optional.empty();
                });
    }
    
    public Optional<Staff> changeOrderStatus(Integer orderId, String newStatus) {
        return repository.findById(orderId)
                .map(order -> {
                    order.setStatus(newStatus);
                    return repository.save(order);
                });
    }
}