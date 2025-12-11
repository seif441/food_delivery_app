package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.OrderStatus;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.repository.OrderRepository;
import com.system.food_delivery_app.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService; // To trigger auto-assignment

    // --- STAFF PROFILE ---
    public Staff getStaffById(Long staffId) {
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
    }

    // --- ORDER MANAGEMENT ---

    // 1. View All Orders (Kitchen Display)
    public List<Order> viewAllOrders() {
        return orderRepository.findAll();
    }

    // 2. View Orders by Specific Status (Generic Filter)
    public List<Order> viewOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // 3. View Pending Orders (Specifically for Kitchen Queue)
    public List<Order> viewPendingOrders(Long staffId) {
        // Validate staff exists first
        getStaffById(staffId);
        return orderRepository.findByStatus(OrderStatus.PENDING);
    }

    // 4. Get Single Order Details
    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // 5. Start Cooking (PENDING -> PREPARING)
    @Transactional
    public Order prepareOrder(Long staffId, Long orderId) {
        getStaffById(staffId); // Validate staff exists
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PREPARING);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Order cannot be prepared. Current status: " + order.getStatus());
        }
    }

    // 6. Finish Cooking (PREPARING -> PREPARED -> Auto Assign Driver)
    @Transactional
    public Order markOutForDelivery(Long orderId) {
        // We call the OrderService because it has the "Find Driver" logic
        return orderService.staffMarkAsPrepared(orderId);
    }

    // 7. Update Status (Manual Override)
    @Transactional
    public Order updateOrderStatus(Long staffId, Long orderId, OrderStatus newStatus) {
        getStaffById(staffId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (newStatus == OrderStatus.PREPARING || 
            newStatus == OrderStatus.PREPARED || 
            newStatus == OrderStatus.CANCELLED) {
            
            order.setStatus(newStatus);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Staff cannot manually set order to " + newStatus);
        }
    }

    // 8. Cancel Order
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.CANCELLED);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Cannot cancel delivered order");
        }
    }
}