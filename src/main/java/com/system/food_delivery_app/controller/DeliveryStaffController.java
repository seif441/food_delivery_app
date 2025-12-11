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
@CrossOrigin(origins = "*")
public class DeliveryStaffController {

    @Autowired
    private DeliveryStaffService deliveryStaffService;

    // GET /api/delivery/{id}/active-orders
    // Driver checks "Do I have any new orders?"
    @GetMapping("/{id}/active-orders")
    public ResponseEntity<List<Order>> getActiveOrders(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.viewAssignedOrders(id));
    }

    // PUT /api/delivery/{driverId}/complete/{orderId}
    // Driver clicks "Delivered" button
    @PutMapping("/{driverId}/complete/{orderId}")
    public ResponseEntity<Order> completeDelivery(@PathVariable Long driverId, @PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryStaffService.completeDelivery(driverId, orderId));
    }

    // GET /api/delivery/{id}/history
    // Driver checks their past jobs
    @GetMapping("/{id}/history")
    public ResponseEntity<List<Order>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.getDeliveryHistory(id));
    }
    
    // PUT /api/delivery/{id}/toggle-availability
    // Driver goes "Offline" or "Online" manually
    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<DeliveryStaff> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryStaffService.toggleAvailability(id));
    }
}