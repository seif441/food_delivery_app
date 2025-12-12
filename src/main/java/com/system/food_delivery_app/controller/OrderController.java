package com.system.food_delivery_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.OrderStatus;
import com.system.food_delivery_app.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") 
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @GetMapping("/statuses")
    public ResponseEntity<OrderStatus[]> getStatuses() {
        return ResponseEntity.ok(OrderStatus.values());
    }

    // =========================================================================
    //  CUSTOMER ENDPOINTS
    // =========================================================================

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        Order newOrder = orderService.placeOrder(order);
        return ResponseEntity.ok(newOrder);
    }

    // --- UPDATED: Added Error Handling for Debugging ---
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            System.out.println("Fetching orders for Customer ID: " + customerId);
            List<Order> orders = orderService.getOrdersByCustomer(customerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            // This prints the REAL error to your IntelliJ/Eclipse Console
            e.printStackTrace(); 
            // This sends the error text to the Frontend so you can read it
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    // =========================================================================
    //  KITCHEN / STAFF ENDPOINTS
    // =========================================================================

    @PutMapping("/{id}/prepared")
    public ResponseEntity<Order> markOrderPrepared(@PathVariable Long id) {
        Order updatedOrder = orderService.staffMarkAsPrepared(id);
        return ResponseEntity.ok(updatedOrder);
    }

    // =========================================================================
    //  ADMIN / GENERAL ENDPOINTS
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}