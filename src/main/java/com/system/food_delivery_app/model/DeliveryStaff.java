package com.system.food_delivery_app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DELIVERY_STAFF")
public class DeliveryStaff extends User {

    // FIXED: Default to FALSE (Offline) so they must manually go online
    private Boolean isAvailable = false;

    public DeliveryStaff() {
        super();
    }

    public Boolean getIsAvailable() {
        return isAvailable != null && isAvailable;
    }

    public void setAvailable(Boolean available) {
        this.isAvailable = available;
    }
}