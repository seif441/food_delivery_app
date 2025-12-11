package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.DeliveryStaff;
import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.OrderStatus;
import com.system.food_delivery_app.repository.DeliveryStaffRepository;
import com.system.food_delivery_app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryStaffService {

    @Autowired
    private DeliveryStaffRepository deliveryStaffRepository;

    @Autowired
    private OrderRepository orderRepository;

    // --- HELPER METHODS ---
    public DeliveryStaff getDeliveryStaffById(Long id) {
        return deliveryStaffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery staff not found"));
    }

    // --- DRIVER ACTIONS ---

    // 1. View Orders Assigned to ME (Only the active one)
    public List<Order> viewAssignedOrders(Long deliveryStaffId) {
        // Validation
        getDeliveryStaffById(deliveryStaffId);
        // Fetch only OUT_FOR_DELIVERY orders assigned to this ID
        return orderRepository.findByDeliveryStaffIdAndStatus(deliveryStaffId, OrderStatus.OUT_FOR_DELIVERY);
    }

    // 2. Mark Order as DELIVERED (Finish the job)
    @Transactional
    public Order completeDelivery(Long deliveryStaffId, Long orderId) {
        // Get Staff
        DeliveryStaff driver = getDeliveryStaffById(deliveryStaffId);
        
        // Get Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Security Check: Is this order actually assigned to this driver?
        if (order.getDeliveryStaff() == null || !order.getDeliveryStaff().getId().equals(deliveryStaffId)) {
            throw new RuntimeException("Access Denied: This order is not assigned to you.");
        }

        // Logic Check: Status
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            // A. Update Order Status
            order.setStatus(OrderStatus.DELIVERED);
            
            // B. IMPORTANT: Make Driver Available Again!
            driver.setAvailable(true);
            
            // C. Save Both
            deliveryStaffRepository.save(driver);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Order is not currently out for delivery.");
        }
    }

    // 3. View My History (Delivered orders)
    public List<Order> getDeliveryHistory(Long deliveryStaffId) {
        getDeliveryStaffById(deliveryStaffId);
        return orderRepository.findByDeliveryStaffIdAndStatus(deliveryStaffId, OrderStatus.DELIVERED);
    }
    
    // 4. Toggle Availability (Manual override if driver wants to take a break)
    public DeliveryStaff toggleAvailability(Long id) {
        DeliveryStaff driver = getDeliveryStaffById(id);
        driver.setAvailable(!driver.isAvailable());
        return deliveryStaffRepository.save(driver);
    }
}