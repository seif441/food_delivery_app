package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.AdminRepository;
import com.system.food_delivery_app.repository.ProductRepository;
import com.system.food_delivery_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {

    private final AdminRepository adminRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public AdminService(AdminRepository adminRepo, UserRepository userRepo, ProductRepository productRepo) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    // Staff management
    public User addStaff(User staff, Role role) {
        staff.getRoles().add(role);
;
        return userRepo.save(staff);
    }

    public User setRole(Long userId, Role role) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
        user.setRole(role);               // update role (e.g., CUSTOMER → STAFF)
        return userRepo.save(user);       // save updated user
    }

    // Delete any user account (customer or staff)
    public void deleteAccount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.deleteById(userId);
    }

    // Menu management
    public Product addMenuItem(Product product) {
        return productRepo.save(product);
    }

    public Product updateMenuItem(Long productId, Product updated) {
        Product product = productRepo.findById(productId).orElseThrow();
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setAvailable(updated.isAvailable());
        product.setImageUrl(updated.getImageUrl());
        return productRepo.save(product);
    }

    public void deleteMenuItem(Long productId) {
        productRepo.deleteById(productId);
    }

    public List<Product> viewMenu() {
        return productRepo.findAll();
    }

    // Price & availability
    public Product updatePrice(Long productId, double newPrice) {
        Product product = productRepo.findById(productId).orElseThrow();
        product.setPrice(newPrice);
        return productRepo.save(product);
    }

    public Product setAvailability(Long productId, boolean available) {
        Product product = productRepo.findById(productId).orElseThrow();
        product.setAvailable(available);
        return productRepo.save(product);
    }

}
