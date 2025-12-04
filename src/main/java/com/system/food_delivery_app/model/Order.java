
package com.system.food_delivery_app.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
@Entity
@Table(name = "[Orders]")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CartItem> items;

    private String paymentMethod = "CASH_ON_DELIVERY";

    private double totalPrice;
    @Enumerated(EnumType.STRING)

    private OrderStatus status;
    // @OneToOne(cascade = CascadeType.ALL)
    // private DeliveryAddress deliveryAddress;

    private LocalDateTime orderDate;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCustomer() {
        return this.customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
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

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // public DeliveryAddress getDeliveryAddress() {
    // return this.deliveryAddress;
    // }

    // public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
    // this.deliveryAddress = deliveryAddress;
    // }

    public LocalDateTime getOrderDate() {
        return this.orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
