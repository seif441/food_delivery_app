package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.service.StaffService;
import com.system.food_delivery_app.model.StaffModel;
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
    public List<StaffModel> viewAllOrders() {
        return staffService.getAllOrders();
    }

    @PutMapping("/orders/{id}/prepare")
    public ResponseEntity<StaffModel> prepareOrder(@PathVariable Integer id) {
        return staffService.prepareOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<StaffModel> updateOrderStatus(
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