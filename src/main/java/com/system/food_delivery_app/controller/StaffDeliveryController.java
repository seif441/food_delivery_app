package com.system.food_delivery_app.controller;


import com.restaurant.model.DeliveryStaff;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.service.DeliveryStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(”/api/delivery-staff”)
@CrossOrigin(origins = “*”)
public class DeliveryStaffController {


@Autowired
private DeliveryStaffService deliveryStaffService;

// Get delivery staff profile
@GetMapping("/{deliveryStaffId}")
public ResponseEntity<?> getDeliveryStaffProfile(@PathVariable Long deliveryStaffId) {
    try {
        DeliveryStaff deliveryStaff = deliveryStaffService.getDeliveryStaffById(deliveryStaffId);
        return ResponseEntity.ok(deliveryStaff);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

// Get all delivery staff
@GetMapping
public ResponseEntity<List<DeliveryStaff>> getAllDeliveryStaff() {
    List<DeliveryStaff> deliveryStaffList = deliveryStaffService.getAllDeliveryStaff();
    return ResponseEntity.ok(deliveryStaffList);
}

// View assigned orders (orders OUT_FOR_DELIVERY)
@GetMapping("/{deliveryStaffId}/assigned-orders")
public ResponseEntity<?> viewAssignedOrders(@PathVariable Long deliveryStaffId) {
    try {
        List<Order> orders = deliveryStaffService.viewAssignedOrders(deliveryStaffId);
        return ResponseEntity.ok(orders);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

// View specific assigned order
@GetMapping("/{deliveryStaffId}/assigned-orders/{orderId}")
public ResponseEntity<?> viewAssignedOrder(@PathVariable Long deliveryStaffId, 
                                           @PathVariable Long orderId) {
    try {
        Order order = deliveryStaffService.viewAssignedOrder(deliveryStaffId, orderId);
        return ResponseEntity.ok(order);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

// Mark order as delivered
@PutMapping("/{deliveryStaffId}/orders/{orderId}/deliver")
public ResponseEntity<?> markAsDelivered(@PathVariable Long deliveryStaffId, 
                                        @PathVariable Long orderId) {
    try {
        Order order = deliveryStaffService.markAsDelivered(deliveryStaffId, orderId);
        return ResponseEntity.ok(Map.of(
            "message", "Order marked as delivered",
            "order", order
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
}

// Update delivery status
@PutMapping("/{deliveryStaffId}/orders/{orderId}/status")
public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long deliveryStaffId, 
                                              @PathVariable Long orderId,
                                              @RequestBody Map<String, String> request) {
    try {
        String statusStr = request.get("status");
        OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        
        Order order = deliveryStaffService.updateDeliveryStatus(deliveryStaffId, orderId, newStatus);
        return ResponseEntity.ok(Map.of(
            "message", "Delivery status updated successfully",
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

// Get delivery history
@GetMapping("/{deliveryStaffId}/delivery-history")
public ResponseEntity<?> getDeliveryHistory(@PathVariable Long deliveryStaffId) {
    try {
        List<Order> deliveredOrders = deliveryStaffService.getDeliveryHistory(deliveryStaffId);
        return ResponseEntity.ok(deliveredOrders);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

// Get active deliveries count
@GetMapping("/{deliveryStaffId}/active-deliveries/count")
public ResponseEntity<?> getActiveDeliveriesCount(@PathVariable Long deliveryStaffId) {
    try {
        Long count = deliveryStaffService.getActiveDeliveriesCount(deliveryStaffId);
        return ResponseEntity.ok(Map.of(
            "deliveryStaffId", deliveryStaffId,
            "activeDeliveriesCount", count
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}


}