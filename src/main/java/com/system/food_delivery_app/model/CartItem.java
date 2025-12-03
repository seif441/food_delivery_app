package com.system.food_delivery_app.model;
import jakarta.persistence.*;
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "Order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "Product_id")
    private Product product;
    private int quantity;
    private double price;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public void calculateSubTotal(){
        if (this.product == null){
            throw new IllegalStateException ("Cannot calculate Subtotal: Product is null..");
        }
        else{
            this.price = this.product.getPrice() * this.quantity;
        }
    }

}
