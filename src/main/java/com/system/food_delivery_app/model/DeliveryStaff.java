package com.system.food_delivery_app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DELIVERY_STAFF")
public class DeliveryStaff extends User {

    // FIXED: Changed from 'boolean' to 'Boolean' to handle NULL values from DB
    private Boolean isAvailable = true;

    public DeliveryStaff() {
        super();
    }

    public Boolean getIsAvailable() {
        // Handle null safely (default to false if null)
        return isAvailable != null && isAvailable;
    }

    public void setAvailable(Boolean available) {
        this.isAvailable = available;
    }
    
    // Keep this for compatibility if other code calls .isAvailable()
    public boolean isAvailable() {
        return isAvailable != null && isAvailable;
    }
}