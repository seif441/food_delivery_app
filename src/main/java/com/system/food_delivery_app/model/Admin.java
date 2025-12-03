package com.system.food_delivery_app.model;

// import com.system.food_delivery_app.model.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin extends User {

    // Admin inherits id, name, email, password, phoneNumber, role from User

    public void addStaff(User staff, Role role) {
        staff.setRole(role);
    }

    public void setRole(User user, Role role) {
        user.setRole(role);
    }

    public void deleteAccount(User user) {
        user.setEmail(null);
        user.setPassword(null);
        user.setPhoneNumber(null);
        user.setName("Deleted");
    }

    // public void updatePrice(Product product, double newPrice) {
    //     product.setPrice(newPrice);
    // }

    // public void setAvailability(Product product, boolean available) {
    //     product.setAvailable(available);
    // }
}
