package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.DeliveryStaff;
import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.OrderStatus;
import com.system.food_delivery_app.repository.DeliveryStaffRepository;
import com.system.food_delivery_app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryStaffRepository deliveryStaffRepository;
    
    @Autowired
    private TrackingService trackingService; // Tracking Service is injected

    private static final double DELIVERY_FEE = 2.99;

    @Transactional
    public Order placeOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place an empty order.");
        }
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new RuntimeException("Order must have a valid Customer.");
        }

        for (var item : order.getItems()) {
            item.setOrder(order);
            item.setCart(null);
        }

        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        double itemsTotal = order.getItems().stream().mapToDouble(item -> item.getPrice()).sum();
        if (itemsTotal == 0) {
             itemsTotal = order.getItems().stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
        }
        order.setTotalPrice(itemsTotal + DELIVERY_FEE);

        Order savedOrder = orderRepository.save(order);
        
        String customerName = (savedOrder.getCustomer() != null) ? savedOrder.getCustomer().getName() : "Unknown";
        trackingService.logEvent("ORDER_PLACED", 
            "Order ID: " + savedOrder.getId() + " placed by " + customerName + " | Total: $" + savedOrder.getTotalPrice());

        return savedOrder;
    }

    @Transactional
    public Order staffMarkAsPrepared(Long orderId) {
        Order order = getOrderById(orderId);
        
        if(order.getStatus() != OrderStatus.OUT_FOR_DELIVERY && order.getStatus() != OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.PREPARED);
            orderRepository.save(order);
            
            // Log 1: KITCHEN READY
            trackingService.logEvent("KITCHEN_READY", 
                "Order ID: " + orderId + " is prepared. Requesting driver assignment...");
            
            // --- FIX 1: Add artificial delay to separate timestamps ---
            try { Thread.sleep(1000); } catch (InterruptedException e) {}

            assignDriverAutomatically(order); 
        }
        return order;
    }

    public void assignDriverAutomatically(Order order) {
        Optional<DeliveryStaff> driverOpt = deliveryStaffRepository.findFirstByIsAvailableTrue();
        if (driverOpt.isPresent()) {
            assignOrderToSpecificDriver(order, driverOpt.get());
        } else {
            trackingService.logEvent("DRIVER_BUSY", 
                "Order ID: " + order.getId() + " is waiting. No drivers available.");
        }
    }

    public void checkAndAssignWaitingOrders(DeliveryStaff driver) {
        List<Order> waitingOrders = orderRepository.findByStatus(OrderStatus.PREPARED);
        for(Order order : waitingOrders) {
            if(order.getDeliveryStaff() == null) {
                assignOrderToSpecificDriver(order, driver);
                break;
            }
        }
    }

    private void assignOrderToSpecificDriver(Order order, DeliveryStaff driver) {
        order.setDeliveryStaff(driver);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        driver.setAvailable(false);
        
        deliveryStaffRepository.save(driver);
        orderRepository.save(order);
        
        // Log 2: DRIVER ASSIGNED
        trackingService.logEvent("DRIVER_ASSIGNED", 
            "Order ID: " + order.getId() + " assigned to Driver: " + driver.getName());
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.PENDING) {
            orderRepository.delete(order); 
            trackingService.logEvent("ORDER_CANCELLED", 
                "Order ID: " + orderId + " was cancelled by customer.");
        } else {
            throw new RuntimeException("Cannot cancel order in progress.");
        }
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    // --- FIX 2: Ensures DELIVERY log and logic run correctly ---
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        
        // Handle delivery completion
        if (status == OrderStatus.DELIVERED) {
            
            // If delivered, set the driver to available
            if (order.getDeliveryStaff() != null) {
                DeliveryStaff driver = order.getDeliveryStaff();
                driver.setAvailable(true);
                deliveryStaffRepository.save(driver);
            }
            
            // Log specifically for delivery (Guaranteed to run if status is DELIVERED)
            String driverName = order.getDeliveryStaff() != null ? order.getDeliveryStaff().getName() : "System";
            trackingService.logEvent("ORDER_DELIVERED", 
                "Order ID: " + orderId + " was delivered by " + driverName);
        } else {
             // General status update log for all other changes
            trackingService.logEvent("STATUS_UPDATE", 
                "Order ID: " + orderId + " changed from " + oldStatus + " to " + status);
        }
            
        return orderRepository.save(order);
    }
}