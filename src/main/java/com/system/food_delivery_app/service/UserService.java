package com.system.food_delivery_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.system.food_delivery_app.model.User;
import com.system.food_delivery_app.repository.UserRepository;


@Service
public class UserService {
      private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register new user
    public User registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepository.save(user);
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
    public User updateUser(Long id, User updatedUser) {
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
    public void deleteUser(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("User id cannot be null");
        }
    }

}
