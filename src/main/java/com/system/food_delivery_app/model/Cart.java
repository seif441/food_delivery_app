package com.system.food_delivery_app.model;
import java.util.*;
import jakarta.persistence.*;
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "Customer_id")
    private Customer customer;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    private double totalPrice;
    @Transient
    private int totalItemQuantity;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<CartItem> getItems() {
        return this.items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalItemQuantity() {
        return this.totalItemQuantity;
    }

    public void setTotalItemQuantity(int totalItemQuantity) {
        this.totalItemQuantity = totalItemQuantity;
    }

    
    public void calculateTotal(){
        this.totalItemQuantity = 0;
        this.totalPrice = 0.0;
        if (items != null){
            for(CartItem item: items){
                totalItemQuantity += item.getQuantity();
                totalPrice += item.getPrice();
            }
        }
    }





}