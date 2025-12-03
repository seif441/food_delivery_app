package com.system.food_delivery_app.model;
import jakarta.persistence.*;

@Entity
public class Staff {

    @Id
    private Integer orderId;
    private String customerName;
    private String deliveryAddress;
    private String status;

    public Staff() {}

    public Staff(Integer orderId, String customerName, String deliveryAddress, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
    }

    public Integer getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCustomerName() { return customerName; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}