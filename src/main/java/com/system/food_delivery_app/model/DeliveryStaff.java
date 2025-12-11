package com.system.food_delivery_app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DELIVERY_STAFF")
public class DeliveryStaff extends User {

    // Tracks if the driver is free to take a new order
    private boolean isAvailable = true;

    // specific field: The type of vehicle (Optional)

    public DeliveryStaff() {
        super();
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}