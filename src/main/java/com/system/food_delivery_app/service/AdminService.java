package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.ProductRepository;
import com.system.food_delivery_app.repository.StaffRepository;
import com.system.food_delivery_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final StaffRepository staffRepository;
    

    public AdminService(UserRepository userRepo, 
                        ProductRepository productRepo, 
                        StaffRepository staffRepository) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.staffRepository = staffRepository;
    }

    // --- STAFF & USER MANAGEMENT ---

    // FIXED: Changed input from User to Staff. 
    // If you pass 'User', JPA saves DTYPE='User'. 
    // You must pass 'Staff' so JPA saves DTYPE='STAFF'.
    public Staff addStaff(Staff staff, Role role) {
        staff.setRole(role);
        return staffRepository.save(staff);
    }

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

    // --- MENU MANAGEMENT (Product Management) ---

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
        
        // If category needs updating, handle it here too
        if (updated.getCategory() != null) {
            product.setCategory(updated.getCategory());
        }
        
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