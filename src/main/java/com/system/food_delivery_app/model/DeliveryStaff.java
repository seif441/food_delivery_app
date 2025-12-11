package com.system.food_delivery_app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DELIVERY_STAFF")
public class DeliveryStaff extends User {

    private boolean isAvailable = true;

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