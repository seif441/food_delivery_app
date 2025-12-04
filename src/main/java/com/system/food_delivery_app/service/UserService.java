package com.system.food_delivery_app.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.UserRepository;
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z]).{8,}$");

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register new user

    public User registerUser(User user) {
        // Validate required fields
        if (user.getEmail() == null || user.getPassword() == null || user.getName() == null) {
            throw new IllegalArgumentException("Name, email, and password are required.");
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // Validate password
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters Long and contain at least one letter.");
        }

        // Hash password before saving
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // User login
    public String loginUser(String email, String password) {
        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // User user = optionalUser.get();

        // Verify password
        // if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        // throw new IllegalArgumentException("Invalid email or password.");
        // }

        // Return JWT or session token (example)
        return "Login successful!"; // Replace with jwtService.generateToken(user)
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

// Update user profile
public User updateProfile(Long id, User updatedUser) {
    if (id == null) {
        throw new IllegalArgumentException("User id cannot be null");
    }
    return userRepository.findById(id)
            .map(user -> {
                user.setName(updatedUser.getName());
                user.setPhoneNumber(updatedUser.getPhoneNumber());
                // user.setAddress(updatedUser.getAddress());
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("User not found"));
}


// Delete user
public void deleteAccount(Long id) {
    if (id != null) {
        userRepository.deleteById(id);
    } else {
        throw new IllegalArgumentException("User id cannot be null");
    }
}

}
