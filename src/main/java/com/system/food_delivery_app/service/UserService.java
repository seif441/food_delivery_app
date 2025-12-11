package com.system.food_delivery_app.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.RoleRepository; // Imported
import com.system.food_delivery_app.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Added RoleRepository

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z]).{8,}$");

    // Updated Constructor to inject RoleRepository
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // --- Register new user (For Customers Only) ---
    public User registerUser(User user) {
        // 1. Validate required fields
        if (user.getEmail() == null || user.getPassword() == null || user.getName() == null) {
            throw new IllegalArgumentException("Name, email, and password are required.");
        }

        // 2. Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // 3. Validate password pattern
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and contain at least one letter.");
        }

        // 4. Assign "CUSTOMER" Role automatically
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role 'CUSTOMER' not found. Please run DataSeeder."));
        
        user.setRole(customerRole);

        // 5. Save and Return
        return userRepository.save(user);
    }

    // --- User login (Returns User object so Frontend can check Role) ---
    public User loginUser(String email, String password) {
        // 1. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // 2. Check Password (Simple comparison for now)
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // 3. Return the User object (Frontend needs this to check roleName)
        return user;
    }

    // --- Other Methods ---

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateProfile(Long id, User updatedUser) {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteAccount(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("User id cannot be null");
        }
    }
}