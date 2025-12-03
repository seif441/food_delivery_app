package com.deliveryapp.demo.Service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {
    
    private final OrderRepository repository;

    @Autowired
    public StaffService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Order> prepareOrder(Integer orderId) {
        return repository.findById(orderId)
                .flatMap(order -> {
                    if ("Pending".equalsIgnoreCase(order.getStatus())) {
                        order.setStatus("Preparing");
                        return Optional.of(repository.save(order));
                    }
                    return Optional.empty();
                });
    }
    
    public Optional<Order> changeOrderStatus(Integer orderId, String newStatus) {
        return repository.findById(orderId)
                .map(order -> {
                    order.setStatus(newStatus);
                    return repository.save(order);
                });
    }
}
