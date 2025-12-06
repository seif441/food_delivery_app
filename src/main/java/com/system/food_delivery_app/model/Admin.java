package com.system.food_delivery_app.model;


import jakarta.persistence.*;

@Entity
public class Admin extends User {


    public void addStaff(User staff, Role role) {
    staff.setRole(role); //SHADY WILL KILL YOU
    }
public void setRole(User user, Role role) {
    user.setRole(role);    
}


    public void updatePrice(Product product, double newPrice) {
        product.setPrice(newPrice);
    }

    public void setAvailability(Product product, boolean available) {
        product.setAvailable(available);
    }
}
