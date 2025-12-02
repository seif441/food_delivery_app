package com.system.food_delivery_app.model;
import jakarta.persistence.*;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // @OneToOne
    // @JoinColumn(name = "Customer_id", referencedColumnName = "id")
    // private customer customer;
    //seif
    

}