package com.system.food_delivery_app.model;


import jakarta.persistence.*;

@Entity
public class Admin extends User {


    public void addStaff(User staff, Role role) {
    staff.getRoles().add(role);   // ✅ add a role to staff
}

public void setRole(User user, Role role) {
    user.getRoles().clear();      // ✅ remove existing roles
    user.getRoles().add(role);    // ✅ assign a new single role
}


    public void updatePrice(Product product, double newPrice) {
        product.setPrice(newPrice);
    }

    public void setAvailability(Product product, boolean available) {
        product.setAvailable(available);
    }
}
