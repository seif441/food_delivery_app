package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.AdminRepository;
import com.system.food_delivery_app.repository.ProductRepository;
import com.system.food_delivery_app.repository.StaffRepository;
import com.system.food_delivery_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private ProductRepository productRepo;
    
    @Autowired
    private StaffRepository staffRepository; // Added to fetch staff list

    // --- STAFF & USER MANAGEMENT ---

    public User addStaff(User staff, Role role) {
        staff.setRole(role);
        return userRepo.save(staff);
    }

    // This is the new method for the Controller to use
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public User setRole(Long userId, Role role) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return userRepo.save(user);
    }

    public void deleteAccount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (!userRepo.existsById(userId)) {
             throw new RuntimeException("User not found");
        }
        userRepo.deleteById(userId);
    }

    // --- MENU MANAGEMENT ---

    public Product addMenuItem(Product product) {
        return productRepo.save(product);
    }

    public Product updateMenuItem(Long productId, Product updated) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
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

    public Product updatePrice(Long productId, double newPrice) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setPrice(newPrice);
        return productRepo.save(product);
    }

    public Product setAvailability(Long productId, boolean available) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setAvailable(available);
        return productRepo.save(product);
    }
}