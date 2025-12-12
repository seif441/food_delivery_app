package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.DeliveryStaff;
import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.service.DeliveryStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(originPatterns = "*", allowCredentials = "true") // Allows JS from other ports to access this
public class DeliveryStaffController {

    @Autowired
    private DeliveryStaffService deliveryStaffService;

    // GET /api/delivery/{id}/active-orders
    @GetMapping("/{id}/active-orders")
    public ResponseEntity<List<Order>> getActiveOrders(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.viewAssignedOrders(id));
    }

    // PUT /api/delivery/{driverId}/complete/{orderId}
    @PutMapping("/{driverId}/complete/{orderId}")
    public ResponseEntity<Order> completeDelivery(@PathVariable Long driverId, @PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryStaffService.completeDelivery(driverId, orderId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryStaff> getDriverProfile(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.getDeliveryStaffById(id));
    }


    // GET /api/delivery/{id}/history
    @GetMapping("/{id}/history")
    public ResponseEntity<List<Order>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.getDeliveryHistory(id));
    }
    
    // PUT /api/delivery/{id}/toggle-availability
    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<DeliveryStaff> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.toggleAvailability(id));
    }
}