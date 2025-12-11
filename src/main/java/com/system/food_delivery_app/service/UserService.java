package com.system.food_delivery_app.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.food_delivery_app.model.Customer; // Import Customer
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.RoleRepository;
import com.system.food_delivery_app.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z]).{8,}$");

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // --- Register new user (Creates a CUSTOMER entity) ---
    public User registerUser(User userData) {
        // 1. Validate required fields
        if (userData.getEmail() == null || userData.getPassword() == null || userData.getName() == null) {
            throw new IllegalArgumentException("Name, email, and password are required.");
        }

        // 2. Check if email already exists
        if (userRepository.findByEmail(userData.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // 3. Validate password
        if (!PASSWORD_PATTERN.matcher(userData.getPassword()).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and contain at least one letter.");
        }

        // --- CRITICAL CHANGE START ---
        
        // 4. Create a CUSTOMER object instead of a generic User
        // This ensures Hibernate writes "CUSTOMER" into the dtype column
        Customer customer = new Customer();
        customer.setName(userData.getName());
        customer.setEmail(userData.getEmail());
        customer.setPassword(userData.getPassword()); // Remember to hash this in production!
        customer.setPhoneNumber(userData.getPhoneNumber());

        // 5. Assign "CUSTOMER" Role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role 'CUSTOMER' not found. Please run DataSeeder."));
        
        customer.setRole(customerRole);

        // 6. Save the CUSTOMER entity
        return userRepository.save(customer);
        
        // --- CRITICAL CHANGE END ---
    }

    // --- User login (Returns User object so Frontend can check Role) ---
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
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