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

        return orderRepository.save(order);
    }

    @Transactional
    public Order staffMarkAsPrepared(Long orderId) {
        Order order = getOrderById(orderId);
        
        // Fix: Idempotency check (if already prepared, just attempt assignment)
        if(order.getStatus() != OrderStatus.OUT_FOR_DELIVERY && order.getStatus() != OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.PREPARED);
            orderRepository.save(order); // Save state first
            assignDriverAutomatically(order); // Try to find driver
        }
        return order;
    }

    // Called when Kitchen finishes food
    public void assignDriverAutomatically(Order order) {
        Optional<DeliveryStaff> driverOpt = deliveryStaffRepository.findFirstByIsAvailableTrue();
        if (driverOpt.isPresent()) {
            assignOrderToSpecificDriver(order, driverOpt.get());
        }
    }

    // NEW: Called when a Driver goes Online
    public void checkAndAssignWaitingOrders(DeliveryStaff driver) {
        // Find oldest order that is PREPARED but has NO driver
        List<Order> waitingOrders = orderRepository.findByStatus(OrderStatus.PREPARED);
        
        for(Order order : waitingOrders) {
            if(order.getDeliveryStaff() == null) {
                assignOrderToSpecificDriver(order, driver);
                break; // Assign one and stop (since driver is now busy)
            }
        }
    }

    private void assignOrderToSpecificDriver(Order order, DeliveryStaff driver) {
        order.setDeliveryStaff(driver);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        driver.setAvailable(false); // Driver becomes busy
        
        deliveryStaffRepository.save(driver);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.PENDING) {
            orderRepository.delete(order); 
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

    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}