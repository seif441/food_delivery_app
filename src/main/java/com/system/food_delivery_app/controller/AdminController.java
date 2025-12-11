package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*") 
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    // --- STAFF MANAGEMENT ---

    @GetMapping("/staff")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(service.getAllStaff());
    }

    // --- FIX IS HERE ---
    // Changed @RequestBody User to @RequestBody Staff
    // This ensures Jackson creates a Staff object (DTYPE="STAFF") instead of a generic User
    @PostMapping("/staff")
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff, @RequestParam Role role) {
        return ResponseEntity.ok(service.addStaff(staff, role));
    }

    @PutMapping("/role/{userId}")
    public ResponseEntity<User> setRole(@PathVariable Long userId, @RequestParam Role role) {
        return ResponseEntity.ok(service.setRole(userId, role));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long userId) {
        service.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    // --- MENU MANAGEMENT ---

    @GetMapping("/menu")
    public ResponseEntity<List<Product>> viewMenu() {
        return ResponseEntity.ok(service.viewMenu());
    }

    @PostMapping("/menu")
    public ResponseEntity<Product> addMenuItem(@RequestBody Product product) {
        return ResponseEntity.ok(service.addMenuItem(product));
    }

    @PutMapping("/menu/{productId}")
    public ResponseEntity<Product> updateMenuItem(@PathVariable Long productId, @RequestBody Product updated) {
        return ResponseEntity.ok(service.updateMenuItem(productId, updated));
    }

    @DeleteMapping("/menu/{productId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long productId) {
        service.deleteMenuItem(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/products/{productId}/price")
    public ResponseEntity<Product> updatePrice(@PathVariable Long productId, @RequestParam double newPrice) {
        return ResponseEntity.ok(service.updatePrice(productId, newPrice));
    }

    @PutMapping("/products/{productId}/availability")
    public ResponseEntity<Product> setAvailability(@PathVariable Long productId, @RequestParam boolean available) {
        return ResponseEntity.ok(service.setAvailability(productId, available));
    }
}