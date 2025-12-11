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

public Staff getStaffById(Long staffId) {
    return staffRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found"));
}

public List<Staff> getAllStaff() {
    return staffRepository.findAll();
}

// public Staff getStaffByEmail(String email) {
//     return staffRepository.findByEmail(email)
//             .orElseThrow(() -> new RuntimeException("Staff not found"));
// }

// View all orders (for staff to manage)
public List<Order> viewAllOrders() {
    return orderRepository.findAll();
}

// View orders by specific status
// public List<Order> viewOrdersByStatus(OrderStatus status) {
//     return orderRepository.findByStatus(status);
// } //SERIAL SHOULD HAVE FIND BY STATUS

// View pending orders
// public List<Order> viewPendingOrders(Long staffId) {
//     Staff staff = getStaffById(staffId);
//     return orderRepository.findByStatus(OrderStatus.PENDING);
// }

// Prepare order - change status to PREPARING
@Transactional
public Order prepareOrder(Long staffId, Long orderId) {
    Staff staff = getStaffById(staffId);
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    
    if (order.getStatus() == OrderStatus.PENDING) {
        staff.prepareOrders(order);
        return orderRepository.save(order);
    } else {
        throw new RuntimeException("Order cannot be prepared. Current status: " + order.getStatus());
    }
}

// Update order status
@Transactional
public Order updateOrderStatus(Long staffId, Long orderId, OrderStatus newStatus) {
    Staff staff = getStaffById(staffId);
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    
    // Staff can update to PREPARING, OUT_FOR_DELIVERY, or CANCELED
    if (newStatus == OrderStatus.PREPARING || 
        newStatus == OrderStatus.OUT_FOR_DELIVERY || 
        newStatus == OrderStatus.CANCELLED) {
        staff.updateOrderStatus(order, newStatus);
        return orderRepository.save(order);
    } else {
        throw new RuntimeException("Staff cannot set order to " + newStatus + " status");
    }
}

// Get order details
public Order getOrderDetails(Long orderId) {
    return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
}

// Mark order as out for delivery
@Transactional
public Order markOutForDelivery(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    
    if (order.getStatus() == OrderStatus.PREPARING) {
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        return orderRepository.save(order);
    } else {
        throw new RuntimeException("Order must be in PREPARING status. Current status: " + order.getStatus());
    }
}

// Cancel order
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
