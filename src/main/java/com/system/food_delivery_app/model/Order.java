package com.system.food_delivery_app.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders") 
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Prevents Lazy Loading crashes
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"orders", "hibernateLazyInitializer", "handler"}) 
    private User customer;

    @ManyToOne
    @JoinColumn(name = "delivery_staff_id")
    @JsonIgnoreProperties({"orders", "hibernateLazyInitializer", "handler"}) 
    private DeliveryStaff deliveryStaff;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CartItem> items;

    private String paymentMethod = "CASH_ON_DELIVERY";

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public User getCustomer() { return this.customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public DeliveryStaff getDeliveryStaff() { return deliveryStaff; }
    public void setDeliveryStaff(DeliveryStaff deliveryStaff) { this.deliveryStaff = deliveryStaff; }

    public List<CartItem> getItems() { return this.items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalPrice() { return this.totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getStatus() { return this.status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getOrderDate() { return this.orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
}