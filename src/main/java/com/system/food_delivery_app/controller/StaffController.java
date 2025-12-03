package com.deliveryapp.demo.controller;

import com.example.model.Order;
import com.example.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    
    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/orders") 
    public List<Order> viewAllOrders() {
        return staffService.getAllOrders();
    }

    @PutMapping("/orders/{id}/prepare")
    public ResponseEntity<Order> prepareOrder(@PathVariable Integer id) {
        return staffService.prepareOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Integer id, 
            @RequestBody StatusUpdate request) {
        
        return staffService.changeOrderStatus(id, request.getNewStatus())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    private static class StatusUpdate {
        private String newStatus;
        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    }
}
