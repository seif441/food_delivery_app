package com.system.food_delivery_app.service;

<<<<<<< HEAD:src/main/java/com/system/food_delivery_app/service/StaffDeliveryService.java
import com.restaurant.model.DeliveryStaff;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.repository.DeliveryStaffRepository;
import com.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
=======
import com.system.food_delivery_app.model.DeliveryStaff;
import com.system.food_delivery_app.repository.DeliveryStaffRepository;
>>>>>>> 5c7cd28108648ea9d2662b36c833c41f66fda277:src/main/java/com/system/food_delivery_app/service/DeliveryStaffService.java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryStaffService {


@Autowired
private DeliveryStaffRepository deliveryStaffRepository;

@Autowired
private OrderRepository orderRepository;

public DeliveryStaff getDeliveryStaffById(Long deliveryStaffId) {
    return deliveryStaffRepository.findById(deliveryStaffId)
            .orElseThrow(() -> new RuntimeException("Delivery staff not found"));
}

public List<DeliveryStaff> getAllDeliveryStaff() {
    return deliveryStaffRepository.findAllDeliveryStaff();
}

public DeliveryStaff getDeliveryStaffByEmail(String email) {
    return deliveryStaffRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Delivery staff not found"));
}

// View assigned orders (OUT_FOR_DELIVERY status)
public List<Order> viewAssignedOrders(Long deliveryStaffId) {
    DeliveryStaff deliveryStaff = getDeliveryStaffById(deliveryStaffId);
    
    // Get all orders with OUT_FOR_DELIVERY status
    List<Order> allOutForDelivery = orderRepository.findByStatus(OrderStatus.OUT_FOR_DELIVERY);
    
    return allOutForDelivery;
}

// View specific assigned order
public Order viewAssignedOrder(Long deliveryStaffId, Long orderId) {
    DeliveryStaff deliveryStaff = getDeliveryStaffById(deliveryStaffId);
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    
    if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY && 
        order.getStatus() != OrderStatus.DELIVERED) {
        throw new RuntimeException("This order is not assigned for delivery");
    }
    
    return order;
}

// Mark order as delivered
@Transactional
public Order markAsDelivered(Long deliveryStaffId, Long orderId) {
    DeliveryStaff deliveryStaff = getDeliveryStaffById(deliveryStaffId);
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    
    if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
        deliveryStaff.updateDeliveryStatus(order);
        return orderRepository.save(order);
    } else {
        throw new RuntimeException("Order must be OUT_FOR_DELIVERY. Current status: " + order.getStatus());
    }
}

// Update delivery status (wrapper method)
@Transactional
public Order updateDeliveryStatus(Long deliveryStaffId, Long orderId, OrderStatus newStatus) {
    DeliveryStaff deliveryStaff = getDeliveryStaffById(deliveryStaffId);
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    
    // Delivery staff can only mark as DELIVERED
    if (newStatus == OrderStatus.DELIVERED && order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
        deliveryStaff.updateDeliveryStatus(order);
        return orderRepository.save(order);
    } else {
        throw new RuntimeException("Delivery staff can only mark OUT_FOR_DELIVERY orders as DELIVERED");
    }
}

// Get delivery history for staff
public List<Order> getDeliveryHistory(Long deliveryStaffId) {
    DeliveryStaff deliveryStaff = getDeliveryStaffById(deliveryStaffId);
    
    // Return all delivered orders (in real app, you'd track which staff delivered which order)
    return orderRepository.findByStatus(OrderStatus.DELIVERED);
}

// Get active deliveries count
public Long getActiveDeliveriesCount(Long deliveryStaffId) {
    DeliveryStaff deliveryStaff = getDeliveryStaffById(deliveryStaffId);
    List<Order> activeDeliveries = orderRepository.findByStatus(OrderStatus.OUT_FOR_DELIVERY);
    return (long) activeDeliveries.size();
}


}
