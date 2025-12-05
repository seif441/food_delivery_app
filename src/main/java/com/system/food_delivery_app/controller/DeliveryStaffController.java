package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.DeliveryStaff;
import com.system.food_delivery_app.service.DeliveryStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryStaffController {
    private final DeliveryStaffService deliveryStaffService;

    public DeliveryStaffController(DeliveryStaffService deliveryStaffService) {
        this.deliveryStaffService = deliveryStaffService;
    }

    @PostMapping
    public ResponseEntity<DeliveryStaff> createDeliveryStaff(@RequestBody DeliveryStaff newDeliveryStaff) {
        return ResponseEntity.status(201).body(deliveryStaffService.createDeliveryStaff(newDeliveryStaff));
    }
    
    @GetMapping
    public ResponseEntity<List<DeliveryStaff>> getAllDeliveryStaff() {
        return ResponseEntity.ok(deliveryStaffService.getAllDeliveryStaff());
    }

    @GetMapping("/{deliveryStaffId}/orders/assigned")
    public ResponseEntity<List<Long>> getAssignedOrders(@PathVariable Long deliveryStaffId) {
        return ResponseEntity.ok(deliveryStaffService.viewAssignedOrders(deliveryStaffId));
    }

    @PutMapping("/{deliveryStaffId}/orders/{orderId}/status")
    public ResponseEntity<String> updateDeliveryStatus(@PathVariable Long deliveryStaffId, @PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryStaffService.updateDeliveryStatus(orderId, deliveryStaffId));
    }
}