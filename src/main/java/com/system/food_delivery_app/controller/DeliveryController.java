package com.example.controller;

import com.example.service.StaffDeliveryService;
import com.example.delivery.model.DeliveryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class StaffDeliveryController {
    
    private final StaffDeliveryService deliveryService;

    @Autowired
    public StaffDeliveryController(StaffDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/assigned-orders")
    public List<DeliveryModel> viewAssignedOrders() {
        return deliveryService.getAssignedOrders();
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<DeliveryModel> updateDeliveryStatus(
            @PathVariable Integer id, 
            @RequestBody StatusUpdate request) {
        
        return deliveryService.updateOrderStatus(id, request.getNewStatus())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
    
    private static class StatusUpdate {
        private String newStatus;
        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    }
}