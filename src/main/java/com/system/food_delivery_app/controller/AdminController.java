package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.Admin;
// import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    // Register admin
    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        return ResponseEntity.ok(service.registerAdmin(admin));
    }

    // Staff management
    @PostMapping("/staff")
    public ResponseEntity<User> addStaff(@RequestBody User staff, @RequestParam Role role) {
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

    // Menu management
    // @PostMapping("/menu")
    // public ResponseEntity<Product> addMenuItem(@RequestBody Product product) {
    //     return ResponseEntity.ok(service.addMenuItem(product));
    // }

    // @PutMapping("/menu/{productId}")
    // public ResponseEntity<Product> updateMenuItem(@PathVariable Long productId, @RequestBody Product updated) {
    //     return ResponseEntity.ok(service.updateMenuItem(productId, updated));
    // }

    @DeleteMapping("/menu/{productId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long productId) {
        service.deleteMenuItem(productId);
        return ResponseEntity.noContent().build();
    }

    // @GetMapping("/menu")
    // public ResponseEntity<List<Product>> viewMenu() {
    //     return ResponseEntity.ok(service.viewMenu());
    // }

    // // Price & availability
    // @PutMapping("/products/{productId}/price")
    // public ResponseEntity<Product> updatePrice(@PathVariable Long productId, @RequestParam double newPrice) {
    //     return ResponseEntity.ok(service.updatePrice(productId, newPrice));
    // }

    // @PutMapping("/products/{productId}/availability")
    // public ResponseEntity<Product> setAvailability(@PathVariable Long productId, @RequestParam boolean available) {
    //     return ResponseEntity.ok(service.setAvailability(productId, available));
    // }
}
