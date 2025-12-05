package com.system.food_delivery_app.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryStaff extends User {
    public DeliveryStaff(String name, String email) {
        super(name, email, "DELIVERY_STAFF");
    }
}