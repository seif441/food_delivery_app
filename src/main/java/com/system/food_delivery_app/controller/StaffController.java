package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.OrderStatus;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // Get staff profile
    @GetMapping("/{staffId}")
    public ResponseEntity<?> getStaffProfile(@PathVariable Long staffId) {
        try {
            Staff staff = staffService.getStaffById(staffId);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // View all orders
    @GetMapping("/{staffId}/orders")
    public ResponseEntity<?> viewAllOrders() {
        try {
            List<Order> orders = staffService.viewAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // View orders by status (e.g., "PREPARED", "PENDING")
    @GetMapping("/{staffId}/orders/status/{status}")
    public ResponseEntity<?> viewOrdersByStatus(@PathVariable Long staffId, @PathVariable String status) {
        try {
            // Validate staff exists (optional but good security)
            staffService.getStaffById(staffId);

            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = staffService.viewOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid status value: " + status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // View pending orders specifically
    @GetMapping("/{staffId}/orders/pending")
    public ResponseEntity<?> viewPendingOrders(@PathVariable Long staffId) {
        try {
            List<Order> orders = staffService.viewPendingOrders(staffId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get specific order details
    @GetMapping("/{staffId}/orders/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            Order order = staffService.getOrderDetails(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Prepare order (change status to PREPARING)
    @PutMapping("/{staffId}/orders/{orderId}/prepare")
    public ResponseEntity<?> prepareOrder(@PathVariable Long staffId, @PathVariable Long orderId) {
        try {
            Order order = staffService.prepareOrder(staffId, orderId);
            return ResponseEntity.ok(Map.of(
                "message", "Order marked as preparing",
                "order", order
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update order status manually
    @PutMapping("/{staffId}/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long staffId, 
                                               @PathVariable Long orderId,
                                               @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
            
            Order order = staffService.updateOrderStatus(staffId, orderId, newStatus);
            return ResponseEntity.ok(Map.of(
                "message", "Order status updated successfully",
                "order", order
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid status value"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Mark order as out for delivery (Calls Auto-Assign)
    @PutMapping("/{staffId}/orders/{orderId}/out-for-delivery")
    public ResponseEntity<?> markOutForDelivery(@PathVariable Long orderId) {
        try {
            Order order = staffService.markOutForDelivery(orderId);
            return ResponseEntity.ok(Map.of(
                "message", "Order marked as PREPARED. Driver assignment started.",
                "order", order
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Cancel order
    @PutMapping("/{staffId}/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            Order order = staffService.cancelOrder(orderId);
            return ResponseEntity.ok(Map.of(
                "message", "Order cancelled successfully",
                "order", order
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}