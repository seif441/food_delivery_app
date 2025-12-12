package com.system.food_delivery_app.model;

import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private double totalPrice;
    
    @Transient
    private int totalItemQuantity;

    // Constant for Delivery Fee
    @Transient // Not saved to DB column, but logic uses it
    private static final double DELIVERY_FEE = 2.99;

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<CartItem> getItems() { return this.items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalPrice() { return this.totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getTotalItemQuantity() { return this.totalItemQuantity; }
    public void setTotalItemQuantity(int totalItemQuantity) { this.totalItemQuantity = totalItemQuantity; }

    /**
     * UPDATED: Calculates total price + $2.99 delivery fee
     */
    public void calculateTotal() {
        this.totalItemQuantity = 0;
        this.totalPrice = 0.0;
        
        if (items != null && !items.isEmpty()) {
            for (CartItem item : items) {
                totalItemQuantity += item.getQuantity();
                totalPrice += item.getPrice();
            }
            
            // LOGIC: Only add delivery fee if there are items in the cart
            if (totalItemQuantity > 0) {
                this.totalPrice += DELIVERY_FEE;
            }
        }
    }
}