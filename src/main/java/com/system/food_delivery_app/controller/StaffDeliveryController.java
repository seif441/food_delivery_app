package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.service.StaffDeliveryService;
import com.system.food_delivery_app.model.Delivery;
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
    public List<Delivery> viewAssignedOrders() {
        return deliveryService.getAssignedOrders();
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @PathVariable Integer id,
            @RequestBody StatusUpdate request) {

        return deliveryService.updateOrderStatus(id, request.getNewStatus())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private static class StatusUpdate {
        private String newStatus;

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }
    }
}