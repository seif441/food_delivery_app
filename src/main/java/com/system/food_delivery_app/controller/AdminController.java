package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.service.AdminService;
import com.system.food_delivery_app.repository.RoleRepository;
import com.system.food_delivery_app.repository.UserRepository; // Import UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
// @CrossOrigin(origins = "*") // Keep this commented out to avoid CORS errors
public class AdminController {

    private final AdminService service;

    @Autowired
    private RoleRepository roleRepository; 

    @Autowired
    private UserRepository userRepository; // NEW: Inject User Repository

    public AdminController(AdminService service) {
        this.service = service;
    }

    // --- USER & STAFF MANAGEMENT ---

    // NEW ENDPOINT: Get All Users (Admin, Customer, Staff, Delivery)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Existing Staff Getter (Optional, but /users covers this now)
    @GetMapping("/staff")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(service.getAllStaff());
    }

    @PostMapping("/staff")
    public ResponseEntity<?> addStaff(@RequestBody Staff staff, @RequestParam String roleName) {
        // 1. Find the role in the DB matching your table (STAFF, DELIVERY_STAFF)
        Optional<Role> roleOpt = roleRepository.findAll().stream()
                .filter(r -> r.getRoleName().equalsIgnoreCase(roleName)) 
                .findFirst();

        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Role '" + roleName + "' does not exist in the database.");
        }

        // 2. Add the staff using the found Role object
        return ResponseEntity.ok(service.addStaff(staff, roleOpt.get()));
    }

    @PutMapping("/role/{userId}")
    public ResponseEntity<User> setRole(@PathVariable Long userId, @RequestParam Role role) {
        return ResponseEntity.ok(service.setRole(userId, role));
    }

@DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId) {
        // 1. Fetch the user to check their role
        java.util.Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 2. Check if the user is an ADMIN
            if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getRoleName())) {
                return ResponseEntity.badRequest().body("Cannot delete an ADMIN account.");
            }
        }

        // 3. Proceed to delete if not Admin
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