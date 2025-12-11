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

    // =========================================================================
    //  1. CUSTOMER ACTIONS
    // =========================================================================

    @Transactional
    public Order placeOrder(Order order) {
        // Calculate total price based on items
        double total = 0;
        if (order.getItems() != null) {
            total = order.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }

        order.setTotalPrice(total);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Initial status

        // Ensure items are linked to this order (for JPA Cascade)
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                item.setOrder(order);
            }
        }

        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.PENDING) {
            orderRepository.delete(order); // Or set status to CANCELLED
        } else {
            throw new RuntimeException("Cannot cancel order. It is already being processed.");
        }
    }

    // =========================================================================
    //  2. KITCHEN STAFF ACTIONS
    // =========================================================================

    /**
     * Called when the kitchen finishes cooking.
     * Sets status to PREPARED and immediately looks for a driver.
     */
    @Transactional
    public Order staffMarkAsPrepared(Long orderId) {
        Order order = getOrderById(orderId);

        // 1. The Staff explicitly changes the status
        order.setStatus(OrderStatus.PREPARING);
        Order savedOrder = orderRepository.save(order);

        // 2. The System reacts by trying to find a driver
        assignDriverAutomatically(savedOrder);

        return savedOrder;
    }

    // =========================================================================
    //  3. SYSTEM INTERNAL LOGIC (Auto-Assignment)
    // =========================================================================

    /**
     * Looks for an available driver.
     * If found: Assigns driver -> Updates Status to OUT_FOR_DELIVERY -> Marks Driver Busy.
     * If not found: Order stays as PREPARED.
     */
    private void assignDriverAutomatically(Order order) {
        // Find the first driver who is available (isAvailable = true)
        Optional<DeliveryStaff> driverOpt = deliveryStaffRepository.findFirstByIsAvailableTrue();

        if (driverOpt.isPresent()) {
            DeliveryStaff driver = driverOpt.get();

            // Link them
            order.setDeliveryStaff(driver);
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY); 

            // Driver is now busy
            driver.setAvailable(false);

            // Save changes to both tables
            deliveryStaffRepository.save(driver);
            orderRepository.save(order);
            
            System.out.println("System assigned Order #" + order.getId() + " to Driver: " + driver.getName());
        } else {
            System.out.println("No drivers available. Order #" + order.getId() + " remains PREPARED.");
        }
    }

    // =========================================================================
    //  4. ADMIN / GENERAL ACTIONS
    // =========================================================================

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    // Manual status update (e.g., for Admin corrections)
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    // Manual Driver Assignment (Admin Override)
    @Transactional
    public Order manualAssignDriver(Long orderId, Long driverId) {
        Order order = getOrderById(orderId);
        DeliveryStaff driver = deliveryStaffRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        order.setDeliveryStaff(driver);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        driver.setAvailable(false);
        
        deliveryStaffRepository.save(driver);
        return orderRepository.save(order);
    }
}