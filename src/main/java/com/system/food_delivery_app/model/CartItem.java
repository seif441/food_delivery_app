package com.system.food_delivery_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items") // Table name: cart_items
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return this.order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return this.product; }
    public void setProduct(Product product) { this.product = product; }

    public Cart getCart() { return this.cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public int getQuantity() { return this.quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return this.price; }
    public void setPrice(double price) { this.price = price; }

    public void calculateSubTotal() {
        if (this.product == null) {
            throw new IllegalStateException("Cannot calculate Subtotal: Product is null..");
        } else {
            this.price = this.product.getPrice() * this.quantity;
        }
    }
}