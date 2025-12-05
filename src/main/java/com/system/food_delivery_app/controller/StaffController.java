package com.system.food_delivery_app.controller;


import com.system.food_delivery_app.dto.StaffRequestDTO;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.service.StaffService;
import org.springframework.http.HttpStatus;
import com.system.food_delivery_app.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }
    
    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff newStaff) {
        return ResponseEntity.status(201).body(staffService.createStaff(newStaff));
    }
    
    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/orders/incoming")
    public ResponseEntity<List<Long>> getIncomingOrders() {
        return ResponseEntity.ok(staffService.viewIncomingOrders());
    }

    @PutMapping("/orders/{orderId}/prepare")
    public ResponseEntity<String> startPreparation(@PathVariable Long orderId) {
        return ResponseEntity.ok(staffService.startPreparation(orderId));
    }
    
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String newStatus) {
        return ResponseEntity.ok(staffService.updateOrderStatus(orderId, newStatus));
    }
}