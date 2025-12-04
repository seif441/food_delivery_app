package com.system.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder

public class Customer extends User {
    
    // One customer can have multiple delivery addresses
    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<DeliveryAddress> addresses;

    // One customer can place multiple orders
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;


}
