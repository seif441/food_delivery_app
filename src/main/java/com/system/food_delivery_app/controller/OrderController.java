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
@CrossOrigin(origins = "*") // Allows your Frontend (React/Angular/Mobile) to connect
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

    // 1. Place a new Order
    // POST /api/orders/place
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        Order newOrder = orderService.placeOrder(order);
        return ResponseEntity.ok(newOrder);
    }

    // 2. Get Order History for a Customer
    // GET /api/orders/customer/5
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // 3. Cancel Order (Only if PENDING)
    // DELETE /api/orders/101
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    // =========================================================================
    //  KITCHEN / STAFF ENDPOINTS
    // =========================================================================

    // 4. Mark Order as Prepared (Triggers Auto-Driver Assignment)
    // PUT /api/orders/101/prepared
    @PutMapping("/{id}/prepared")
    public ResponseEntity<Order> markOrderPrepared(@PathVariable Long id) {
        // Staff clicks "Ready" -> System finds Driver -> Status becomes OUT_FOR_DELIVERY
        Order updatedOrder = orderService.staffMarkAsPrepared(id);
        return ResponseEntity.ok(updatedOrder);
    }

    // =========================================================================
    //  ADMIN / GENERAL ENDPOINTS
    // =========================================================================

    // 5. Get Single Order Details
    // GET /api/orders/101
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // 6. Get All Orders (Admin Dashboard)
    // GET /api/orders/all
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 7. Manual Status Update (Admin Override)
    // PUT /api/orders/101/status?status=DELIVERED
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}