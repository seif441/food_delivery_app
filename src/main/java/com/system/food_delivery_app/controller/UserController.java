package com.system.food_delivery_app.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.system.food_delivery_app.dto.LoginRequest;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired // Best practice: Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- User Registration ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException ex) {
            // Return specific error message (e.g., "Email already registered")
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            // Catch generic runtime exceptions (like "Role not found")
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // --- User Login (UPDATED) ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Updated: Now receives a User object instead of String token
            User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // --- Get All Users ---
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // --- Get User by Email ---
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Update User Profile ---
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateProfile(id, user));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Delete User ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}